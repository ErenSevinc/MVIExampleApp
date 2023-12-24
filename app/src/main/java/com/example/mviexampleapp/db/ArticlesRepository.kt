package com.example.mviexampleapp.db

import com.example.mviexampleapp.model.Articles
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ArticlesRepository @Inject constructor(
    private val dao: ArticleDao
) {

    fun insert(articles: Articles) = dao.insert(articles)
    fun delete(articles: Articles) = dao.delete(articles)
    fun getFavArticles(): List<Articles> = dao.getFavArticles()

}