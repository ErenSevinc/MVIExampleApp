package com.example.mviexampleapp.network

import com.example.mviexampleapp.model.NewsResponse
import com.example.mviexampleapp.utils.Resource
import java.lang.Exception
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getHeadlineNews(country: String? = null, category: String? = null, apiKey: String? = null)
    : Resource<NewsResponse> {
       return try {
            val result = apiService.getHeadlineNews(category = category)
            Resource.Success(data = result)
        } catch (e: Exception) {
            Resource.Error(errorMessage = e.message.toString())
        }
    }
}