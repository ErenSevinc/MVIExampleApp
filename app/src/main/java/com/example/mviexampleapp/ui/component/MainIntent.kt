package com.example.mviexampleapp.ui.component

import com.example.mviexampleapp.model.Articles

sealed class MainIntent {
    //API
    class GetNews(val category: String): MainIntent()
    //DATABASE
    object GetFavNews : MainIntent()
    class InsertNews(val articles: Articles, val category: String): MainIntent()
    class DeleteNews(val articles: Articles, val category: String ?= null): MainIntent()
    //SEARCH
    class SearchNews(val query: String) : MainIntent()
}