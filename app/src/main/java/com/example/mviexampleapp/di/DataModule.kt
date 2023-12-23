package com.example.mviexampleapp.di

import android.app.Application
import androidx.room.Room
import com.example.mviexampleapp.db.ArticlesRepository
import com.example.mviexampleapp.db.ArticlesRepositoryImpl
import com.example.mviexampleapp.db.MyDatabase
import com.example.mviexampleapp.network.ApiRepository
import com.example.mviexampleapp.network.ApiService
import com.example.mviexampleapp.utils.Constant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .create()
    }

    @Singleton
    @Provides
    fun provideService(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): ApiService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
            .create(ApiService::class.java)
    }

    @Provides
    fun provideRepository(apiService: ApiService): ApiRepository {
        return ApiRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideMyDataBase(app: Application): MyDatabase {
        return Room.databaseBuilder(
            app,
            MyDatabase::class.java,
            "MyDataBase"
        ).build()
    }


    @Provides
    @Singleton
    fun provideMyRepository(mydb: MyDatabase): ArticlesRepository {
        return ArticlesRepositoryImpl(mydb.dao)
    }
}