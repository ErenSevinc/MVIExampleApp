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
    fun insert(articles: Articles)

    @Query("DELETE FROM articles WHERE id = :id")
    fun delete(id: Int)

    @Query("SELECT * FROM articles")
    fun getFavArticles(): List<Articles>
}