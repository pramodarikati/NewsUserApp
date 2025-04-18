package com.example.newsuserapp.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("apiKey") apiKey: String = "be87b8a0b8bc446399f9d082ebf84da4"
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 10,
        @Query("apiKey") apiKey: String = "be87b8a0b8bc446399f9d082ebf84da4"
    ): Response<NewsResponse>
}
