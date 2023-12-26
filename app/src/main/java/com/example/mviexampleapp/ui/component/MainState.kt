package com.example.mviexampleapp.ui.component

import com.example.mviexampleapp.model.Articles


sealed class MainState {
    object Idle: MainState()
    object Loading: MainState()
    data class News(val news: List<Articles>): MainState()
    data class Error(val error: String?): MainState()
}