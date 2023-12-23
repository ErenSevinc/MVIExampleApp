package com.example.mviexampleapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mviexampleapp.model.Articles
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articles: Articles)

    @Delete
    suspend fun delete(studentEntity: Articles)

    @Query("SELECT * FROM articles")
    fun getFavArticles(): Flow<List<Articles>>
}