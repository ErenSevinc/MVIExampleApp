package com.example.mviexampleapp.ui.component

sealed class MainScreen(val route: String) {
    object NewsList: MainScreen(route = "NewsListPage")
    object NewsDetail: MainScreen(route = "NewsDetailPage")
}
