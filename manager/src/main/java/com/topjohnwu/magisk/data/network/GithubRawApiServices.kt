package com.topjohnwu.magisk.data.network

import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.model.entity.MagiskConfig
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming


interface GithubRawApiServices {

    //region topjohnwu/magisk_files

    @GET("$MAGISK_FILES/master/stable.json")
    fun fetchConfig(): Single<MagiskConfig>

    @GET("$MAGISK_FILES/master/beta.json")
    fun fetchBetaConfig(): Single<MagiskConfig>

    @GET("$MAGISK_FILES/master/canary_builds/release.json")
    fun fetchCanaryConfig(): Single<MagiskConfig>

    @GET("$MAGISK_FILES/master/canary_builds/canary.json")
    fun fetchCanaryDebugConfig(): Single<MagiskConfig>

    @GET("$MAGISK_FILES/{$REVISION}/snet.apk")
    @Streaming
    fun fetchSnet(@Path(REVISION) revision: String = Constants.SNET_REVISION): Single<ResponseBody>

    @GET("$MAGISK_FILES/{$REVISION}/bootctl")
    @Streaming
    fun fetchBootctl(@Path(REVISION) revision: String = Constants.BOOTCTL_REVISION): Single<ResponseBody>

    //endregion

    //region topjohnwu/Magisk/master

    @GET("$MAGISK_MASTER/scripts/module_installer.sh")
    @Streaming
    fun fetchModuleInstaller(): Single<ResponseBody>

    //endregion

    //region Magisk-Modules-Repo

    @GET("$MAGISK_MODULES/{$MODULE}/master/{$FILE}")
    @Streaming
    fun fetchFile(id: String, file: String): Single<ResponseBody>

    //endregion


    companion object {
        private const val REVISION = "revision"
        private const val MODULE = "module"
        private const val FILE = "file"


        private const val MAGISK_FILES = "topjohnwu/magisk_files"
        private const val MAGISK_MASTER = "topjohnwu/Magisk/master"
        private const val MAGISK_MODULES = "Magisk-Modules-Repo"
    }

}
