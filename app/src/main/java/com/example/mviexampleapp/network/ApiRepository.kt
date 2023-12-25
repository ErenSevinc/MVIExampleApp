package com.example.mviexampleapp.network

import com.example.mviexampleapp.model.NewsResponse
import com.example.mviexampleapp.utils.Constant
import com.example.mviexampleapp.utils.Resource
import java.lang.Exception
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getHeadlineNews(
        country: String? = null,
        category: String? = null,
        apiKey: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    )
            : Resource<NewsResponse> {
        return try {
            if (category != null) {
                val result = apiService.getHeadlineNews(category = category)
                Resource.Success(data = result)
            } else {
                val result = apiService.getHeadlineNews(category = Constant.CATEGORY_GENERAL)
                Resource.Success(data = result)
            }
        } catch (e: Exception) {
            Resource.Error(errorMessage = e.message.toString())
        }
    }
}