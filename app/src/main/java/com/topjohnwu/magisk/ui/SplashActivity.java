package com.topjohnwu.magisk.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.topjohnwu.magisk.BuildConfig;
import com.topjohnwu.magisk.ClassMap;
import com.topjohnwu.magisk.Config;
import com.topjohnwu.magisk.Const;
import com.topjohnwu.magisk.R;
import com.topjohnwu.magisk.data.database.RepoDatabaseHelper;
import com.topjohnwu.magisk.tasks.CheckUpdates;
import com.topjohnwu.magisk.tasks.UpdateRepos;
import com.topjohnwu.magisk.ui.base.BaseActivity;
import com.topjohnwu.magisk.utils.LocaleManager;
import com.topjohnwu.magisk.utils.Utils;
import com.topjohnwu.magisk.view.Notifications;
import com.topjohnwu.magisk.view.Shortcuts;
import com.topjohnwu.net.Networking;
import com.topjohnwu.superuser.Shell;

import androidx.appcompat.app.AlertDialog;

public class SplashActivity extends BaseActivity {

    public static boolean DONE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shell.getShell(shell -> {
            if (Config.magiskVersionCode > 0 &&
                    Config.magiskVersionCode < Const.MAGISK_VER.MIN_SUPPORT) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.unsupport_magisk_title)
                        .setMessage(R.string.unsupport_magisk_message)
                        .setNegativeButton(R.string.ok, null)
                        .setOnDismissListener(dialog -> finish())
                        .show();
            } else {
                initAndStart();
            }
        });
    }

    private void initAndStart() {
        String pkg = Config.get(Config.Key.SU_MANAGER);
        if (pkg != null && getPackageName().equals(BuildConfig.APPLICATION_ID)) {
            Config.remove(Config.Key.SU_MANAGER);
            Shell.su("pm uninstall " + pkg).submit();
        }
        if (TextUtils.equals(pkg, getPackageName())) {
            try {
                // We are the manager, remove com.topjohnwu.magisk as it could be malware
                getPackageManager().getApplicationInfo(BuildConfig.APPLICATION_ID, 0);
                Shell.su("pm uninstall " + BuildConfig.APPLICATION_ID).submit();
            } catch (PackageManager.NameNotFoundException ignored) {}
        }

        // Dynamic detect all locales
        LocaleManager.loadAvailableLocales(R.string.app_changelog);

        // Set default configs
        Config.initialize();

        // Create notification channel on Android O
        Notifications.setup(this);

        // Schedule periodic update checks
        Utils.scheduleUpdateCheck();
        CheckUpdates.check();

        // Setup shortcuts
        Shortcuts.setup(this);

        // Create repo database
        app.repoDB = new RepoDatabaseHelper(this);

        // Magisk working as expected
        if (Shell.rootAccess() && Config.magiskVersionCode > 0) {
            // Load modules
            Utils.loadModules(false);
            // Load repos
            if (Networking.checkNetworkStatus(this))
                new UpdateRepos().exec();
        }

        Intent intent = new Intent(this, ClassMap.get(MainActivity.class));
        intent.putExtra(Const.Key.OPEN_SECTION, getIntent().getStringExtra(Const.Key.OPEN_SECTION));
        DONE = true;
        startActivity(intent);
        finish();
    }
}
