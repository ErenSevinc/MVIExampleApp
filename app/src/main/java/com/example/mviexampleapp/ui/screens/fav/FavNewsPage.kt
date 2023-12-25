package com.example.mviexampleapp.ui.screens.fav

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mviexampleapp.R
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.component.MainScreen
import com.example.mviexampleapp.ui.component.MainState
import com.example.mviexampleapp.ui.screens.list.LoadingScreen
import com.example.mviexampleapp.ui.screens.list.NewsItem
import com.example.mviexampleapp.ui.screens.list.NewsListViewModel
import com.example.mviexampleapp.utils.toDate
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavNewsPage(navContorller: NavController) {

    val viewModel: FavNewsViewModel = hiltViewModel()
    val searchText = viewModel.searchText.collectAsState()
    val favNews = viewModel.favNews.collectAsState()
    val isSearch = viewModel.isSearch.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()

    LaunchedEffect(favNews) {
        if (favNews == null) {
            viewModel.getFavArticles()
        }
    }

    with(viewModel) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextField(
                value = searchText.value,
                onValueChange = this@with::onSearchTextChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                placeholder = {
                    Text(
                        text = "Search"
                    )
                })
            Spacer(modifier = Modifier.height(16.dp))
            if (isSearch.value) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                Text(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    text = "Fav News",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                LazyColumn(modifier = Modifier.padding(top = 2.dp)) {
                    items(items = favNews.value ?: emptyList()) { item ->
                        FavNewsCardItem(news = item) {
                            val encodedUrl =
                                URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                            navContorller.navigate(MainScreen.NewsDetail.route + "/" + encodedUrl)
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FavNewsCardItem(news: Articles, onClick: (url: String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(370.dp)
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    ) {
        Log.d("api res", news.toString())
        Column(modifier = Modifier
            .padding(4.dp)
            .clickable { onClick.invoke(news.url ?: "https://www.google.com") }
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                painter = painterResource(id = R.drawable.news),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 15.dp)) {
                Text(
                    text = news.title ?: "",
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Divider(
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = news.publishedAt?.toDate() ?: "", color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
            }

        }
    }
}