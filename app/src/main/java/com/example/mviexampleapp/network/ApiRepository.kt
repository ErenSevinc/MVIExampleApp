package com.example.mviexampleapp.network

import com.example.mviexampleapp.model.NewsResponse
import com.example.mviexampleapp.utils.Constant
import com.example.mviexampleapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiService: ApiService) {

    fun getHeadlineNews(
        category: String? = null,
    ): Flow<Resource<NewsResponse>> {
        return flow {
            emit(Resource.Loading)
            val response = if (category != null) {
                apiService.getHeadlineNews(category = category)
            } else {
                apiService.getHeadlineNews(category = Constant.CATEGORY_GENERAL)
            }
            emit(Resource.Success(response))
        }.catch {
            emit(Resource.Error(errorMessage = it.message))
        }.flowOn(Dispatchers.IO)
    }
}