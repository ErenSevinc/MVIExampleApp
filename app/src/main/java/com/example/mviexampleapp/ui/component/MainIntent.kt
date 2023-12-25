package com.example.mviexampleapp.ui.component

import com.example.mviexampleapp.model.Articles

sealed class MainIntent {
    class GetNews(val category: String): MainIntent()
    object GetFavNews : MainIntent()
    class InsertNews(val articles: Articles, val category: String): MainIntent()
    class DeleteNews(val articles: Articles, val category: String): MainIntent()
}