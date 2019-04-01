package com.topjohnwu.magisk.data.network

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming


interface RepoApiServices {

    @GET("master/scripts/module_installer.sh")
    @Streaming
    fun fetchModuleInstaller(): Single<ResponseBody>

}
