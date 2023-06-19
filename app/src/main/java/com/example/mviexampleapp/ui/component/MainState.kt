package com.example.mviexampleapp.ui.component


import com.example.mviexampleapp.model.NewsResponse


sealed class MainState {
    object Idle: MainState()
    object Loading: MainState()
    data class News(val news: NewsResponse): MainState()
    data class Error(val error: String?): MainState()
}