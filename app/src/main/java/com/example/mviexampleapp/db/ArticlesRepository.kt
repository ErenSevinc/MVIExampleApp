package com.example.mviexampleapp.db

import com.example.mviexampleapp.model.Articles
import kotlinx.coroutines.flow.Flow

interface ArticlesRepository {

    suspend fun insert(articles: Articles)

    suspend fun delete(articles: Articles)

    suspend fun getFavArticles(): Flow<List<Articles>>
}