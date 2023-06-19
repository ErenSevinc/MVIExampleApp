package com.example.mviexampleapp.di

import com.example.mviexampleapp.network.ApiRepository
import com.example.mviexampleapp.network.ApiService
import com.example.mviexampleapp.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)
    }

    @Provides
    fun provideRepository(apiService: ApiService): ApiRepository {
        return ApiRepository(apiService)
    }
}