#include <sys/stat.h>
#include <sys/types.h>
#include <sys/mount.h>
#include <sys/sendfile.h>
#include <sys/sysmacros.h>
#include <linux/input.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <dirent.h>
#include <fcntl.h>
#include <libgen.h>
#include <functional>
#include <string_view>

#include <xz.h>
#include <magisk.h>
#include <magiskpolicy.h>
#include <selinux.h>
#include <cpio.h>
#include <utils.h>
#include <flags.h>

#include "binaries.h"
#ifdef USE_64BIT
#include "binaries_arch64.h"
#else
#include "binaries_arch.h"
#endif
#include "magiskrc.h"

#define DEFAULT_DT_DIR "/proc/device-tree/firmware/android"

static int test_main(int argc, char *argv[]);

constexpr const char *init_applet[] =
		{ "magiskpolicy", "supolicy", "init_test", nullptr };
constexpr int (*init_applet_main[])(int, char *[]) =
		{ magiskpolicy_main, magiskpolicy_main, test_main, nullptr };

struct cmdline {
	bool system_as_root;
	char slot[3];
	char dt_dir[128];
};

struct device {
	dev_t major;
	dev_t minor;
	char devname[32];
	char partname[32];
	char path[64];
};

struct raw_data {
	void *buf;
	size_t sz;
};

static bool unxz(int fd, const uint8_t *buf, size_t size) {
	uint8_t out[8192];
	xz_crc32_init();
	struct xz_dec *dec = xz_dec_init(XZ_DYNALLOC, 1 << 26);
	struct xz_buf b = {
			.in = buf,
			.in_pos = 0,
			.in_size = size,
			.out = out,
			.out_pos = 0,
			.out_size = sizeof(out)
	};
	enum xz_ret ret;
	do {
		ret = xz_dec_run(dec, &b);
		if (ret != XZ_OK && ret != XZ_STREAM_END)
			return false;
		write(fd, out, b.out_pos);
		b.out_pos = 0;
	} while (b.in_pos != size);
	return true;
}

static void decompress_ramdisk() {
	constexpr char tmp[] = "tmp.cpio";
	constexpr char ramdisk_xz[] = "ramdisk.cpio.xz";
	if (access(ramdisk_xz, F_OK))
		return;
	uint8_t *buf;
	size_t sz;
	mmap_ro(ramdisk_xz, buf, sz);
	int fd = open(tmp, O_WRONLY | O_CREAT | O_TRUNC | O_CLOEXEC);
	unxz(fd, buf, sz);
	munmap(buf, sz);
	close(fd);
	cpio_mmap cpio(tmp);
	cpio.extract();
	unlink(tmp);
	unlink(ramdisk_xz);
}

static int dump_magisk(const char *path, mode_t mode) {
	int fd = open(path, O_WRONLY | O_CREAT | O_TRUNC | O_CLOEXEC, mode);
	if (fd < 0)
		return 1;
	if (!unxz(fd, magisk_xz, sizeof(magisk_xz)))
		return 1;
	close(fd);
	return 0;
}

static int dump_manager(const char *path, mode_t mode) {
	int fd = open(path, O_WRONLY | O_CREAT | O_TRUNC | O_CLOEXEC, mode);
	if (fd < 0)
		return 1;
	if (!unxz(fd, manager_xz, sizeof(manager_xz)))
		return 1;
	close(fd);
	return 0;
}

class MagiskInit {
private:
	cmdline cmd{};
	raw_data self{};
	raw_data config{};
	int root = -1;
	char **argv;
	bool load_sepol = false;
	bool mnt_system = false;
	bool mnt_vendor = false;
	bool mnt_product = false;
	bool mnt_odm = false;

	void load_kernel_info();
	void preset();
	void early_mount();
	void setup_rootfs();
	bool read_dt_fstab(const char *mnt_point, char *partname, char *partfs);
	bool patch_sepolicy();
	void cleanup();

public:
	explicit MagiskInit(char *argv[]) : argv(argv) {}
	void setup_overlay();
	void re_exec_init();
	void start();
	void test();
};

static inline void parse_cmdline(const std::function<void (std::string_view, const char *)> &fn) {
	char cmdline[4096];
	int fd = open("/proc/cmdline", O_RDONLY | O_CLOEXEC);
	cmdline[read(fd, cmdline, sizeof(cmdline))] = '\0';
	close(fd);

	char *tok, *eql, *tmp, *saveptr;
	saveptr = cmdline;
	while ((tok = strtok_r(nullptr, " \n", &saveptr)) != nullptr) {
		eql = strchr(tok, '=');
		if (eql) {
			*eql = '\0';
			if (eql[1] == '"') {
				tmp = strchr(saveptr, '"');
				if (tmp != nullptr) {
					*tmp = '\0';
					saveptr[-1] = ' ';
					saveptr = tmp + 1;
					eql++;
				}
			}
			fn(tok, eql + 1);
		} else {
			fn(tok, "");
		}
	}
}

#define test_bit(bit, array) (array[bit / 8] & (1 << (bit % 8)))

static bool check_key_combo() {
	uint8_t bitmask[(KEY_MAX + 1) / 8];
	int eventfd = -1;

	for (int minor = 64; minor < 96; ++minor) {
		if (mknod("/event", S_IFCHR | 0444, makedev(13, minor))) {
			PLOGE("mknod");
			continue;
		}
		eventfd = xopen("/event", O_RDWR | O_CLOEXEC);
		unlink("/event");
		if (eventfd < 0)
			continue;
		memset(bitmask, 0, sizeof(bitmask));
		ioctl(eventfd, EVIOCGBIT(EV_KEY, sizeof(bitmask)), bitmask);
		if (test_bit(KEY_POWER, bitmask) && test_bit(KEY_VOLUMEUP, bitmask)) {
			// Check KEY_POWER because KEY_VOLUMEUP could be headphone input
			break;
		}
	}

	if (eventfd < 0)
		return false;

	// Return true if volume key up is hold for more than 3 seconds
	int count = 0;
	for (int i = 0; i < 500; ++i) {
		memset(bitmask, 0, sizeof(bitmask));
		ioctl(eventfd, EVIOCGKEY(sizeof(bitmask)), bitmask);
		count = test_bit(KEY_VOLUMEUP, bitmask) ? count + 1 : 0;
		if (count >= 300) {
			close(eventfd);
			return true;
		}
		// Check every 10ms
		usleep(10000);
	}
	close(eventfd);
	return false;
}

void MagiskInit::load_kernel_info() {
	// Communicate with kernel using procfs and sysfs
	mkdir("/proc", 0755);
	xmount("proc", "/proc", "proc", 0, nullptr);
	mkdir("/sys", 0755);
	xmount("sysfs", "/sys", "sysfs", 0, nullptr);

	bool enter_recovery = false;
	bool kirin = false;
	bool recovery_mode = false;

	parse_cmdline([&](auto key, auto value) -> void {
		LOGD("cmdline: [%s]=[%s]\n", key.data(), value);
		if (key == "androidboot.slot_suffix") {
			strcpy(cmd.slot, value);
		} else if (key == "androidboot.slot") {
			cmd.slot[0] = '_';
			strcpy(cmd.slot + 1, value);
		} else if (key == "skip_initramfs") {
			cmd.system_as_root = true;
		} else if (key == "androidboot.android_dt_dir") {
			strcpy(cmd.dt_dir, value);
		} else if (key == "enter_recovery") {
			enter_recovery = value[0] == '1';
		} else if (key == "androidboot.hardware") {
			kirin = strstr(value, "kirin") || strstr(value, "hi3660");
		}
	});

	parse_prop_file("/.backup/.magisk", [&](auto key, auto value) -> bool {
		if (key == "RECOVERYMODE" && value == "true")
			recovery_mode = true;
		return true;
	});

	if (kirin && enter_recovery) {
		// Inform that we are actually booting as recovery
		if (!recovery_mode) {
			if (FILE *f = fopen("/.backup/.magisk", "ae"); f) {
				fprintf(f, "RECOVERYMODE=true\n");
				fclose(f);
			}
			recovery_mode = true;
		}
	}

	if (recovery_mode)
		cmd.system_as_root = !check_key_combo();

	if (cmd.dt_dir[0] == '\0')
		strcpy(cmd.dt_dir, DEFAULT_DT_DIR);

	LOGD("system_as_root[%d]\nslot[%s]\ndt_dir[%s]\n", cmd.system_as_root, cmd.slot, cmd.dt_dir);
}

void MagiskInit::preset() {
	root = open("/", O_RDONLY | O_CLOEXEC);

	if (cmd.system_as_root) {
		// Clear rootfs
		frm_rf(root, { "overlay", "proc", "sys" });
	} else {
		decompress_ramdisk();

		// Revert original init binary
		rename("/.backup/init", "/init");
		rm_rf("/.backup");

		// Do not go further if device is booting into recovery
		if (access("/sbin/recovery", F_OK) == 0)
			re_exec_init();
	}
}

static inline void parse_device(struct device *dev, const char *uevent) {
	dev->partname[0] = '\0';
	FILE *fp = xfopen(uevent, "r");
	char buf[64];
	while (fgets(buf, sizeof(buf), fp)) {
		if (strncmp(buf, "MAJOR", 5) == 0) {
			sscanf(buf, "MAJOR=%ld", (long*) &dev->major);
		} else if (strncmp(buf, "MINOR", 5) == 0) {
			sscanf(buf, "MINOR=%ld", (long*) &dev->minor);
		} else if (strncmp(buf, "DEVNAME", 7) == 0) {
			sscanf(buf, "DEVNAME=%s", dev->devname);
		} else if (strncmp(buf, "PARTNAME", 8) == 0) {
			sscanf(buf, "PARTNAME=%s", dev->partname);
		}
	}
	fclose(fp);
	LOGD("%s [%s] (%u, %u)\n", dev->devname, dev->partname, (unsigned) dev->major, (unsigned) dev->minor);
}

static bool setup_block(struct device *dev, const char *partname) {
	char path[128];
	struct dirent *entry;
	DIR *dir = opendir("/sys/dev/block");
	if (dir == nullptr)
		return false;
	bool found = false;
	while ((entry = readdir(dir))) {
		if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0)
			continue;
		sprintf(path, "/sys/dev/block/%s/uevent", entry->d_name);
		parse_device(dev, path);
		if (strcasecmp(dev->partname, partname) == 0) {
			sprintf(dev->path, "/dev/block/%s", dev->devname);
			found = true;
			break;
		}
	}
	closedir(dir);

	if (!found)
		return false;

	mkdir("/dev", 0755);
	mkdir("/dev/block", 0755);
	mknod(dev->path, S_IFBLK | 0600, makedev(dev->major, dev->minor));
	return true;
}

static inline bool is_lnk(const char *name) {
	struct stat st;
	if (lstat(name, &st))
		return false;
	return S_ISLNK(st.st_mode);
}

#define link_root(part) \
if (is_lnk("/system_root" part)) \
	cp_afc("/system_root" part, part)

#define mount_root(part) \
if (!is_lnk("/" #part) && read_dt_fstab(#part, partname, fstype)) { \
	setup_block(&dev, partname); \
	xmkdir("/" #part, 0755); \
	xmount(dev.path, "/" #part, fstype, MS_RDONLY, nullptr); \
	mnt_##part = true; \
}

void MagiskInit::early_mount() {
	struct device dev;
	char partname[32];
	char fstype[32];

	if (cmd.system_as_root) {
		sprintf(partname, "system%s", cmd.slot);
		setup_block(&dev, partname);
		xmkdir("/system_root", 0755);
		xmount(dev.path, "/system_root", "ext4", MS_RDONLY, nullptr);
		xmkdir("/system", 0755);
		xmount("/system_root/system", "/system", nullptr, MS_BIND, nullptr);

		// Android Q
		if (is_lnk("/system_root/init"))
			load_sepol = true;

		// System-as-root with monolithic sepolicy
		if (access("/system_root/sepolicy", F_OK) == 0)
			cp_afc("/system_root/sepolicy", "/sepolicy");

		// Copy if these partitions are symlinks
		link_root("/vendor");
		link_root("/product");
		link_root("/odm");
	} else {
		mount_root(system);
	}

	mount_root(vendor);
	mount_root(product);
	mount_root(odm);
}

void MagiskInit::setup_rootfs() {
	bool patch_init = patch_sepolicy();

	if (cmd.system_as_root) {
		// Clone rootfs
		int system_root = open("/system_root", O_RDONLY | O_CLOEXEC);
		clone_dir(system_root, root, false);
		close(system_root);
	}

	if (patch_init) {
		constexpr char SYSTEM_INIT[] = "/system/bin/init";
		// If init is symlink, copy it to rootfs so we can patch
		if (is_lnk("/init"))
			cp_afc(SYSTEM_INIT, "/init");

		char *addr;
		size_t size;
		mmap_rw("/init", addr, size);
		for (char *p = addr; p < addr + size; ++p) {
			if (memcmp(p, SPLIT_PLAT_CIL, sizeof(SPLIT_PLAT_CIL)) == 0) {
				// Force init to load /sepolicy
				memset(p, 'x', sizeof(SPLIT_PLAT_CIL) - 1);
				p += sizeof(SPLIT_PLAT_CIL) - 1;
			} else if (memcmp(p, SYSTEM_INIT, sizeof(SYSTEM_INIT)) == 0) {
				// Force execute /init instead of /system/bin/init
				strcpy(p, "/init");
				p += sizeof(SYSTEM_INIT) - 1;
			}
		}
		munmap(addr, size);
	}

	// Handle ramdisk overlays
	int fd = open("/overlay", O_RDONLY | O_CLOEXEC);
	if (fd >= 0) {
		mv_dir(fd, root);
		close(fd);
		rmdir("/overlay");
	}

	// Patch init.rc
	FILE *rc = xfopen("/init.p.rc", "we");
	file_readline("/init.rc", [&](auto line) -> bool {
		// Do not start vaultkeeper
		if (str_contains(line, "start vaultkeeper"))
			return true;
		// Do not run flash_recovery
		if (str_starts(line, "service flash_recovery")) {
			fprintf(rc, "service flash_recovery /system/bin/xxxxx\n");
			return true;
		}
		// Else just write the line
		fprintf(rc, "%s", line.data());
		return true;
	});
	char pfd_svc[8], ls_svc[8];
	gen_rand_str(pfd_svc, sizeof(pfd_svc));
	do {
		gen_rand_str(ls_svc, sizeof(ls_svc));
	} while (strcmp(pfd_svc, ls_svc) == 0);
	fprintf(rc, magiskrc, pfd_svc, pfd_svc, ls_svc);
	fclose(rc);
	clone_attr("/init.rc", "/init.p.rc");
	rename("/init.p.rc", "/init.rc");

	// Don't let init run in init yet
	lsetfilecon("/init", "u:object_r:rootfs:s0");

	// Create hardlink mirror of /sbin to /root
	mkdir("/root", 0750);
	clone_attr("/sbin", "/root");
	int rootdir = xopen("/root", O_RDONLY | O_CLOEXEC);
	int sbin = xopen("/sbin", O_RDONLY | O_CLOEXEC);
	link_dir(sbin, rootdir);
}

bool MagiskInit::patch_sepolicy() {
	bool patch_init = false;

	if (access(SPLIT_PLAT_CIL, R_OK) == 0)
		patch_init = true;                    /* Split sepolicy */
	else if (access("/sepolicy", R_OK) == 0)
		load_policydb("/sepolicy");           /* Monolithic sepolicy */
	else
		return false;                         /* No SELinux */

	// Mount selinuxfs to communicate with kernel
	xmount("selinuxfs", SELINUX_MNT, "selinuxfs", 0, nullptr);

	if (patch_init)
		load_split_cil();

	sepol_magisk_rules();
	sepol_allow(SEPOL_PROC_DOMAIN, ALL, ALL, ALL);
	dump_policydb("/sepolicy");

	// Load policy to kernel so we can label rootfs
	if (load_sepol)
		dump_policydb(SELINUX_LOAD);

	// Remove OnePlus stupid debug sepolicy and use our own
	if (access("/sepolicy_debug", F_OK) == 0) {
		unlink("/sepolicy_debug");
		link("/sepolicy", "/sepolicy_debug");
	}

	// Enable selinux functions
	selinux_builtin_impl();

	return patch_init;
}

bool MagiskInit::read_dt_fstab(const char *mnt_point, char *partname, char *partfs) {
	char path[128];
	int fd;
	sprintf(path, "%s/fstab/%s/dev", cmd.dt_dir, mnt_point);
	if ((fd = xopen(path, O_RDONLY | O_CLOEXEC)) >= 0) {
		read(fd, path, sizeof(path));
		close(fd);
		char *name = rtrim(strrchr(path, '/') + 1);
		sprintf(partname, "%s%s", name, strend(name, cmd.slot) ? cmd.slot : "");
		sprintf(path, "%s/fstab/%s/type", cmd.dt_dir, mnt_point);
		if ((fd = xopen(path, O_RDONLY | O_CLOEXEC)) >= 0) {
			read(fd, partfs, 32);
			close(fd);
			return true;
		}
	}
	return false;
}

#define umount_root(part) \
if (mnt_##part) \
	umount("/" #part);

void MagiskInit::cleanup() {
	umount(SELINUX_MNT);
	umount("/sys");
	umount("/proc");
	umount_root(system);
	umount_root(vendor);
	umount_root(product);
	umount_root(odm);
}

static inline void patch_socket_name(const char *path) {
	uint8_t *buf;
	char name[sizeof(MAIN_SOCKET)];
	size_t size;
	mmap_rw(path, buf, size);
	for (int i = 0; i < size; ++i) {
		if (memcmp(buf + i, MAIN_SOCKET, sizeof(MAIN_SOCKET)) == 0) {
			gen_rand_str(name, sizeof(name));
			memcpy(buf + i, name, sizeof(name));
			i += sizeof(name);
		}
		if (memcmp(buf + i, LOG_SOCKET, sizeof(LOG_SOCKET)) == 0) {
			gen_rand_str(name, sizeof(name));
			memcpy(buf + i, name, sizeof(name));
			i += sizeof(name);
		}
	}
	munmap(buf, size);
}

void MagiskInit::setup_overlay() {
	char path[128];
	int fd;

	// Wait for early-init start
	while (access(EARLYINIT, F_OK) != 0)
		usleep(10);
	setcon("u:r:" SEPOL_PROC_DOMAIN ":s0");
	unlink(EARLYINIT);

	// Mount the /sbin tmpfs overlay
	xmount("tmpfs", "/sbin", "tmpfs", 0, nullptr);
	chmod("/sbin", 0755);
	setfilecon("/sbin", "u:object_r:rootfs:s0");

	// Dump binaries
	mkdir(MAGISKTMP, 0755);
	fd = xopen(MAGISKTMP "/config", O_WRONLY | O_CREAT, 0000);
	write(fd, config.buf, config.sz);
	close(fd);
	fd = xopen("/sbin/magiskinit", O_WRONLY | O_CREAT, 0755);
	write(fd, self.buf, self.sz);
	close(fd);
	dump_magisk("/sbin/magisk", 0755);
	patch_socket_name("/sbin/magisk");
	setfilecon("/sbin/magisk", "u:object_r:" SEPOL_FILE_DOMAIN ":s0");
	setfilecon("/sbin/magiskinit", "u:object_r:" SEPOL_FILE_DOMAIN ":s0");

	// Create applet symlinks
	for (int i = 0; applet_names[i]; ++i) {
		sprintf(path, "/sbin/%s", applet_names[i]);
		xsymlink("/sbin/magisk", path);
	}
	for (int i = 0; init_applet[i]; ++i) {
		sprintf(path, "/sbin/%s", init_applet[i]);
		xsymlink("/sbin/magiskinit", path);
	}

	// Create symlinks pointing back to /root
	DIR *dir = xopendir("/root");
	struct dirent *entry;
	fd = xopen("/sbin", O_RDONLY);
	while((entry = xreaddir(dir))) {
		if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0)
			continue;
		snprintf(path, PATH_MAX, "/root/%s", entry->d_name);
		xsymlinkat(path, fd, entry->d_name);
	}
	closedir(dir);
	close(fd);

	close(xopen(EARLYINITDONE, O_RDONLY | O_CREAT, 0));
	exit(0);
}

void MagiskInit::re_exec_init() {
	cleanup();
	execv("/init", argv);
	exit(1);
}

void MagiskInit::start() {
	// Prevent file descriptor confusion
	mknod("/null", S_IFCHR | 0666, makedev(1, 3));
	int null = open("/null", O_RDWR | O_CLOEXEC);
	unlink("/null");
	xdup3(null, STDIN_FILENO, O_CLOEXEC);
	xdup3(null, STDOUT_FILENO, O_CLOEXEC);
	xdup3(null, STDERR_FILENO, O_CLOEXEC);
	if (null > STDERR_FILENO)
		close(null);

	load_kernel_info();

	full_read("/init", &self.buf, &self.sz);
	full_read("/.backup/.magisk", &config.buf, &config.sz);

	preset();
	early_mount();
	setup_rootfs();
}

void MagiskInit::test() {
	cmdline_logging();
	log_cb.ex = nop_ex;

	chdir(dirname(argv[0]));
	chroot(".");
	chdir("/");

	load_kernel_info();
	preset();
	early_mount();
	setup_rootfs();
	cleanup();
}

static int test_main(int argc, char *argv[]) {
	MagiskInit init(argv);
	init.test();
	return 0;
}

int main(int argc, char *argv[]) {
	umask(0);

	for (int i = 0; init_applet[i]; ++i) {
		if (strcmp(basename(argv[0]), init_applet[i]) == 0)
			return (*init_applet_main[i])(argc, argv);
	}

	if (argc > 1 && strcmp(argv[1], "-x") == 0) {
		if (strcmp(argv[2], "magisk") == 0)
			return dump_magisk(argv[3], 0755);
		else if (strcmp(argv[2], "manager") == 0)
			return dump_manager(argv[3], 0644);
	}

	if (getpid() != 1)
		return 1;

	MagiskInit init(argv);

	// Run the main routine
	init.start();

	// Close all file descriptors
	for (int i = 0; i < 30; ++i)
		close(i);

	// Launch daemon to setup overlay
	if (fork_dont_care() == 0)
		init.setup_overlay();

	init.re_exec_init();
}
