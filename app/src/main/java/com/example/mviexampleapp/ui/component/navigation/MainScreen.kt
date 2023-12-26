package com.example.mviexampleapp.ui.component.navigation

sealed class MainScreen(val route: String) {
    object NewsList: MainScreen(route = "NewsListPage")
    object NewsDetail: MainScreen(route = "NewsDetailPage")
    object Favourites: MainScreen(route = "FavouriteNewsPage")
}
