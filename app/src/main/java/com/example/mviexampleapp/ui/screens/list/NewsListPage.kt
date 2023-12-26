package com.example.mviexampleapp.ui.screens.list

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.component.MainState
import com.example.mviexampleapp.ui.component.ui.CategoryList
import com.example.mviexampleapp.ui.component.ui.LoadingScreen
import com.example.mviexampleapp.ui.component.ui.NewsList
import com.example.mviexampleapp.utils.Constant
import kotlinx.coroutines.launch


@Composable
fun NewsListPage(navContorller: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: NewsListViewModel = hiltViewModel()
    val screenState = viewModel.state.collectAsState()
    val selectedCategory = remember { mutableStateOf(Constant.CATEGORY_GENERAL) }
    val list = listOf(
        Constant.CATEGORY_GENERAL,
        Constant.CATEGORY_BUSINESS,
        Constant.CATEGORY_ENTERTAINMENT,
        Constant.CATEGORY_HEALTH,
        Constant.CATEGORY_SCIENCE,
        Constant.CATEGORY_SPORTS,
        Constant.CATEGORY_TECHNOLOGY
    )
    val selectedIndex = remember { mutableStateOf(list.indexOf(selectedCategory.value)) }

    LaunchedEffect(screenState) {
        if (screenState.value == MainState.Idle) {
            viewModel.userIntent.send(MainIntent.GetFavNews)
            viewModel.userIntent.send(MainIntent.GetNews(selectedCategory.value))
        }
    }

    with(screenState) {
        val result = this.value

        AnimatedVisibility(
            visible = result is MainState.Loading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LoadingScreen()
        }
        AnimatedVisibility(
            visible = result is MainState.News,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            (result as? MainState.News)?.news?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CategoryList(viewModel, list, selectedCategory, selectedIndex)
                    NewsList(news = it, navContorller = navContorller, onFavClick = {
                        coroutineScope.launch {
                            if (!it.isFavourite) {
                                viewModel.userIntent.send(MainIntent.InsertNews(articles = it, category = selectedCategory.value))
                            } else {
                                viewModel.userIntent.send(MainIntent.DeleteNews(articles = it, category = selectedCategory.value))
                            }
                        }
                    })
                }
            }
        }
        AnimatedVisibility(visible = result is MainState.Error) {
            Log.e("Errorss", (result as? MainState.Error)?.error ?: "")
            Toast.makeText(
                LocalContext.current,
                (result as? MainState.Error)?.error ?: "",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
}