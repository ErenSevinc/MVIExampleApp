package com.example.mviexampleapp.ui.screens.list

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.component.MainScreen
import com.example.mviexampleapp.ui.component.MainState
import com.example.mviexampleapp.utils.Constant
import com.example.mviexampleapp.utils.toDate
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun NewsListPage(navContorller: NavController, newsVM: NewsListViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 65.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //CategoryList()
        CategoryList(vm = newsVM, navContorller = navContorller)
    }
}

@Composable
fun Greeting(vm: NewsListViewModel, modifier: Modifier = Modifier, navContorller: NavController) {
    when (val state = vm.state.value) {
        is MainState.Idle -> {

        }

        is MainState.Loading -> {
            LoadingScreen()
        }

        is MainState.Error -> {
            Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_LONG).show()
        }

        is MainState.News -> {
            state.news.articles?.let {
                NewsList(news = it, navContorller = navContorller)
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CategoryList(vm: NewsListViewModel, modifier: Modifier = Modifier, navContorller: NavController) {
    val list =
        listOf(
            Constant.CATEGORY_BUSINESS,
            Constant.CATEGORY_ENTERTAINMENT,
            Constant.CATEGORY_GENERAL,
            Constant.CATEGORY_HEALTH,
            Constant.CATEGORY_SCIENCE,
            Constant.CATEGORY_SPORTS,
            Constant.CATEGORY_TECHNOLOGY
        )
    val selectedCategoty = remember { mutableStateOf(Constant.CATEGORY_GENERAL) }
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        LazyRow {
            items(items = list) {
                OutlinedButton(
                    onClick = {
                        selectedCategoty.value = it
                        vm.getNews(it)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selectedCategoty.value == it) Color.DarkGray else Color.LightGray,
                        contentColor = if (selectedCategoty.value == it) Color.White else Color.DarkGray
                    ),
                    border = BorderStroke(1.dp, Color.DarkGray),
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = it.capitalize())
                }
            }
        }
        Greeting(vm = vm, navContorller = navContorller)
    }
}

@Composable
fun NewsList(news: List<Articles>, navContorller: NavController) {
    LazyColumn {
        items(items = news) { item ->
            NewsItem(item) {
                val encodedUrl = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                navContorller.navigate(MainScreen.NewsDetail.route + "/" + encodedUrl)
            }
            Divider(color = Color.LightGray, modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
        }
    }
}

@Composable
fun NewsItem(news: Articles, onClick: (url: String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick.invoke(news.url ?: "https://www.google.com") }
    ) {
        val url = news.url

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp)
        ) {
            news.title?.let {
                Text(text = it, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomStart
            ) {
                news.publishedAt?.let {
                    Text(text = it.toDate())
                }
            }
        }
    }
}