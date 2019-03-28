package com.topjohnwu.magisk.data.network

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiServices {

    @GET
    @Streaming
    fun fetchZip(@Url url: String): Single<ResponseBody>

}
