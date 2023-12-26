package com.example.mviexampleapp.ui.component.state

import com.example.mviexampleapp.model.Articles

data class NewsListState(
    val loading: Boolean= false,
    val news : List<Articles> = emptyList(),
    val errorMessage: String?= null
)

