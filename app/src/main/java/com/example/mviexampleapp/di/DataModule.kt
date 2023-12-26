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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

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
//            .addMigrations(migration)
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


//    companion object {
//        val migration = object: Migration(1,2) {
//            override fun migrate(database: SupportSQLiteDatabase){
//                database.execSQL("ALTER TABLE articles ADD COLUMN isFavouriteNew INTEGER DEFAULT 0 NOT NULL")
//            }
//        }
//    }
}