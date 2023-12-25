package com.example.mviexampleapp.ui.screens.fav

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.component.MainScreen
import com.example.mviexampleapp.ui.component.MainState
import com.example.mviexampleapp.ui.screens.list.LoadingScreen
import com.example.mviexampleapp.ui.screens.list.NewsItem
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun FavNewsPage(navContorller: NavController) {

    val viewModel: FavNewsViewModel = hiltViewModel()
    val screenState = viewModel.state.collectAsState()

    LaunchedEffect(screenState) {
        if (screenState.value == MainState.Idle) {
            viewModel.userIntent.send(MainIntent.GetFavNews)
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
        AnimatedVisibility(visible = result is MainState.Error) {
            Log.e("Xx-Error-xX", (result as? MainState.Error)?.error ?: "")
            Toast.makeText(
                LocalContext.current,
                (result as? MainState.Error)?.error ?: "",
                Toast.LENGTH_LONG
            )
                .show()
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

                    Column() {
                        Text(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .align(Alignment.CenterHorizontally),
                            text = "Fav News",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LazyColumn(modifier = Modifier.padding(top = 2.dp)) {
                            items(items = it) { item ->
                                NewsItem(item) {
                                    val encodedUrl = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                                    navContorller.navigate(MainScreen.NewsDetail.route + "/" + encodedUrl)
                                }
                                Divider(
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}