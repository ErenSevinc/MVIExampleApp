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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: NewsListViewModel = hiltViewModel()
    val screenState = viewModel.state.collectAsState()
    val selectedCategory = rememberSaveable { mutableStateOf(Constant.CATEGORY_GENERAL) }
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

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->

            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    coroutineScope.launch {
                        viewModel.userIntent.send(NewsListIntent.GetFavNews)
                        viewModel.userIntent.send(NewsListIntent.GetNews(selectedCategory.value))
                    }
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    with(screenState.value) {
        if (news.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CategoryList(viewModel, list, selectedCategory, selectedIndex)
                NewsList(news = news, navContorller = navContorller, onFavClick = {
                    coroutineScope.launch {
                        if (!it.isFavourite) {
                            viewModel.userIntent.send(
                                NewsListIntent.InsertNews(
                                    articles = it,
                                    category = selectedCategory.value
                                )
                            )
                        } else {
                            viewModel.userIntent.send(
                                NewsListIntent.DeleteNews(
                                    articles = it,
                                    category = selectedCategory.value
                                )
                            )
                        }
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
            Log.e("Errorss", errorMessage ?: "")
            Toast.makeText(
                LocalContext.current,
                errorMessage ?: "",
                Toast.LENGTH_LONG
            )
                .show()
        }
    }
}