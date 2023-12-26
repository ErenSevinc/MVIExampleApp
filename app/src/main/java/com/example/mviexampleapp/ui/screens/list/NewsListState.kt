package com.example.mviexampleapp.ui.screens.list

import com.example.mviexampleapp.model.Articles

data class NewsListState(
    val loading: Boolean= false,
    val news : List<Articles> = emptyList(),
    val errorMessage: String?= null
)

