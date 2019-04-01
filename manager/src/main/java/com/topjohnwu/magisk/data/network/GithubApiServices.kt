package com.topjohnwu.magisk.data.network

import retrofit2.http.GET
import retrofit2.http.Query


interface GithubApiServices {

    @GET("users/Magisk-Modules-Repo/repos")
    fun fetchRepos(@Query("page") page: Int, @Query("per_page") count: Int = 100, @Query("sort") sortOrder: String = "pushed")

}
