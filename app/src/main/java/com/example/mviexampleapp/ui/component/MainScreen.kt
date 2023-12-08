package com.example.mviexampleapp.ui.component

sealed class MainScreen(val route: String) {
    // TODO
    object NewsList: MainScreen(route = "NewsListPage")
    object NewsDetail: MainScreen(route = "NewsDetailPage")
    object Favourites: MainScreen(route = "FavouriteNewsPage")
}
