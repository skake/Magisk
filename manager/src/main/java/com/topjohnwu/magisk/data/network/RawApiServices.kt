package com.topjohnwu.magisk.data.network

import com.topjohnwu.magisk.Constants
import com.topjohnwu.magisk.model.entity.MagiskConfig
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming


interface RawApiServices {

    @GET("master/stable.json")
    fun fetchConfig(): Single<MagiskConfig>

    @GET("master/beta.json")
    fun fetchBetaConfig(): Single<MagiskConfig>

    @GET("master/canary_builds/release.json")
    fun fetchCanaryConfig(): Single<MagiskConfig>

    @GET("master/canary_builds/canary.json")
    fun fetchCanaryDebugConfig(): Single<MagiskConfig>

    @GET("${Constants.SNET_REVISION}/snet.apk")
    @Streaming
    fun fetchSnet(): Single<ResponseBody>

    @GET("${Constants.BOOTCTL_REVISION}/bootctl")
    @Streaming
    fun fetchBootctl(): Single<ResponseBody>
}
