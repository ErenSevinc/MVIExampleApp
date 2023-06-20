package com.example.mviexampleapp.ui.component

sealed class MainIntent {
    class GetNews(val category: String): MainIntent()
}