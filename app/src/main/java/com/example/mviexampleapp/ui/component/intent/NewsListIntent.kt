package com.example.mviexampleapp.ui.component.intent

import com.example.mviexampleapp.model.Articles

sealed class NewsListIntent {
    class GetNews(val category: String): NewsListIntent()
    object GetFavNews : NewsListIntent()
    class InsertNews(val articles: Articles, val category: String): NewsListIntent()
    class DeleteNews(val articles: Articles, val category: String ?= null): NewsListIntent()
}