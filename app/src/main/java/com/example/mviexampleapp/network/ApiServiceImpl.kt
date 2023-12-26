package com.example.mviexampleapp.network

import com.example.mviexampleapp.model.NewsResponse
import com.example.mviexampleapp.utils.Constant
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import javax.inject.Inject

class ApiServiceImpl @Inject constructor(private val httpClient: HttpClient) : ApiService {

    override suspend fun getHeadlineNews(
        country: String?,
        category: String?,
        apiKey: String?,
        page: Int?,
        pageSize : Int?
    ): NewsResponse {
        return httpClient.get(Constant.BASE_URL) {
            contentType(ContentType.Application.Json)
            url {
                path("/v2/top-headlines")
                parameter(key = "country", value = country)
                parameter(key = "category", value = category)
                parameter(key = "apiKey", value = apiKey)
                parameter(key = "page", value = page)
                parameter(key = "pageSize", value = pageSize)
            }
        }.body()
    }
}