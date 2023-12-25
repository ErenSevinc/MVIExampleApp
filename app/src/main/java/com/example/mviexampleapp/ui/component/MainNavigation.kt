package com.example.mviexampleapp.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mviexampleapp.ui.screens.detail.NewsDetailPage
import com.example.mviexampleapp.ui.screens.fav.FavNewsPage
import com.example.mviexampleapp.ui.screens.list.NewsListPage
import com.example.mviexampleapp.ui.screens.list.NewsListViewModel

@Composable
fun MainNavigation(
    navController: NavHostController,
    toolbarTitle: MutableState<String>,
    paddingValues: PaddingValues
) {

    NavHost(modifier = Modifier.padding(paddingValues),navController = navController, startDestination = MainScreen.NewsList.route) {
        composable(route = MainScreen.NewsList.route) {
            toolbarTitle.value = "News List"
            NewsListPage(navController)
        }
        composable(
            route = MainScreen.NewsDetail.route + "/{url}",
            arguments = listOf(navArgument("url") {
                type = NavType.StringType
            })
        ){
            toolbarTitle.value = "News Detail"
            NewsDetailPage( it.arguments?.getString("url") ?: "")
        }
        composable(
            route = MainScreen.Favourites.route,
        ){
            toolbarTitle.value = "Fav News"
            FavNewsPage(navController)
        }
    }
}