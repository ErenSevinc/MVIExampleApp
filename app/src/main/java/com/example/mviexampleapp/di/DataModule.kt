package com.example.mviexampleapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mviexampleapp.db.AppDatabase
import com.example.mviexampleapp.db.ArticleDao
import com.example.mviexampleapp.db.ArticlesRepository
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.network.ApiRepository
import com.example.mviexampleapp.network.ApiService
import com.example.mviexampleapp.network.ApiServiceImpl
import com.example.mviexampleapp.utils.Constant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

//    @Provides
//    @Singleton
//    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
//        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//    }
//
//    @Provides
//    @Singleton
//    fun provideOkHttpClient(
//        httpLoggingInterceptor: HttpLoggingInterceptor
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(httpLoggingInterceptor)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideGson(): Gson {
//        return GsonBuilder()
//            .create()
//    }
//
//    @Singleton
//    @Provides
//    fun provideService(
//        okHttpClient: OkHttpClient,
//        gson: Gson
//    ): ApiService {
//        return Retrofit.Builder()
//            .client(okHttpClient)
//            .baseUrl(Constant.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson)).build()
//            .create(ApiService::class.java)
//    }
//
//    @Provides
//    fun provideRepository(apiService: ApiService): ApiRepository {
//        return ApiRepository(apiService)
//    }


    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json( Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
    }

    @Singleton
    @Provides
    fun provideApiService(httpClient: HttpClient):ApiService = ApiServiceImpl(httpClient)

    @Provides
    fun provideRepository(apiService: ApiService): ApiRepository {
        return ApiRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideMyDataBase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "MyDataBase"
        )
            .addMigrations(migration)
            .build()
    }

    @Provides
    @Singleton
    fun provideDao(appDatabase: AppDatabase) : ArticleDao{
        return appDatabase.dao()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(dao: ArticleDao): ArticlesRepository {
        return ArticlesRepository(dao)
    }

    @Provides
    fun provideEntity() = Articles()


    companion object {
        val migration = object: Migration(1,2) {
            override fun migrate(database: SupportSQLiteDatabase){
                database.execSQL("ALTER TABLE articles DD COLUMN createdAt TEXT")
            }
        }
    }
}