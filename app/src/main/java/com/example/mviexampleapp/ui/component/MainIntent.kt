package com.example.mviexampleapp.ui.component

sealed class MainIntent {
    object GetNews: MainIntent()
}