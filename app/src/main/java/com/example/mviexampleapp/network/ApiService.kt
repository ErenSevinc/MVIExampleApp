package com.example.mviexampleapp.network

import com.example.mviexampleapp.model.NewsResponse
import com.example.mviexampleapp.utils.Constant
import com.example.mviexampleapp.utils.Resource
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("top-headlines")
    suspend fun getHeadlineNews(
        @Query("country")country: String? = Constant.COUNTRY,
        @Query("category")category: String? = Constant.CATEGORY_GENERAL,
        @Query("apiKey")apiKey: String? = Constant.API_KEY
    ): NewsResponse
    //https://newsapi.org/v2/everything?q=aziz%20y%C4%B1ld%C4%B1r%C4%B1m&from=2023-06-17&to=2023-06-18&sortBy=popularity&apiKey=fbdd503109ee45168ec05390dd6b6379
}