package com.example.mviexampleapp.ui.component.intent

import com.example.mviexampleapp.model.Articles

sealed class FavNewsIntent {
    object GetFavNews : FavNewsIntent()
    class DeleteNews(val articles: Articles, val category: String ?= null): FavNewsIntent()
    class SearchNews(val query: String) : FavNewsIntent()
}