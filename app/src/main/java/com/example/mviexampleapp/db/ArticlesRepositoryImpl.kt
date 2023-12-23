package com.example.mviexampleapp.db

import com.example.mviexampleapp.model.Articles
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArticlesRepositoryImpl @Inject constructor(
    private val dao: ArticleDao,
) : ArticlesRepository {

    override suspend fun insert(articles: Articles) {
        withContext(IO) {
            dao.insert(articles)
        }
    }

    override suspend fun delete(articles: Articles) {
        withContext(IO) {
            dao.delete(articles)
        }
    }

    override suspend fun getFavArticles(): Flow<List<Articles>> {
        return withContext(IO) {
            dao.getFavArticles()
        }
    }
}