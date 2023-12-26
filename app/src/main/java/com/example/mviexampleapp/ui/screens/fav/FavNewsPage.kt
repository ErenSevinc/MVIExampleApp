package com.example.mviexampleapp.ui.screens.fav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mviexampleapp.ui.component.intent.FavNewsIntent
import com.example.mviexampleapp.ui.component.ui.ErrorScreen
import com.example.mviexampleapp.ui.component.ui.LoadingScreen
import com.example.mviexampleapp.ui.component.ui.NewsList
import com.example.mviexampleapp.ui.component.ui.SearchBar
import kotlinx.coroutines.launch


@Composable
fun FavNewsPage(navContorller: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: FavNewsViewModel = hiltViewModel()
    val screenState = viewModel.favNewsState.collectAsState()

    LaunchedEffect(screenState) {
        viewModel.userIntent.send(FavNewsIntent.GetFavNews)
    }

    with(screenState.value) {

        AnimatedVisibility(
            visible = news.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SearchBar() {
                    coroutineScope.launch {
                        viewModel.userIntent.send(FavNewsIntent.SearchNews(it))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                NewsList(news = news, navContorller = navContorller, onFavClick = {
                    coroutineScope.launch {
                        viewModel.userIntent.send(FavNewsIntent.DeleteNews(articles = it))
                    }
                })
            }

        }

        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LoadingScreen()
        }

        AnimatedVisibility(visible = errorMessage != null) {
            ErrorScreen(errorMessge = errorMessage ?: "")
        }
    }
}