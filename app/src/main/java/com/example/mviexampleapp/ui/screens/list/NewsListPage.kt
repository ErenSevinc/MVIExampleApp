package com.example.mviexampleapp.ui.screens.list

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mviexampleapp.model.Articles
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mviexampleapp.R
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.component.MainScreen
import com.example.mviexampleapp.ui.component.MainState
import com.example.mviexampleapp.utils.Constant
import com.example.mviexampleapp.utils.toDate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.coroutines.coroutineContext


@Composable
fun NewsListPage(navContorller: NavController) {
    val viewModel: NewsListViewModel = hiltViewModel()
    val screenState = viewModel.state.collectAsState()
    val selectedCategoty = remember { mutableStateOf(Constant.CATEGORY_GENERAL) }
    val list = listOf(
        Constant.CATEGORY_GENERAL,
        Constant.CATEGORY_BUSINESS,
        Constant.CATEGORY_ENTERTAINMENT,
        Constant.CATEGORY_HEALTH,
        Constant.CATEGORY_SCIENCE,
        Constant.CATEGORY_SPORTS,
        Constant.CATEGORY_TECHNOLOGY
    )
    val selectedIndex = remember { mutableStateOf(list.indexOf(selectedCategoty.value)) }

    LaunchedEffect(screenState) {
        if (screenState.value == MainState.Idle) {
            viewModel.userIntent.send(MainIntent.GetNews(selectedCategoty.value))
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
            (result as? MainState.News)?.news?.articles?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CategoryList(viewModel, list, selectedCategoty, selectedIndex)
                    NewsList(news = it, navContorller = navContorller, viewModel)
                }
            }
        }
        AnimatedVisibility(visible = result is MainState.Error) {
            Toast.makeText(
                LocalContext.current,
                (result as? MainState.Error)?.error ?: "",
                Toast.LENGTH_LONG
            )
                .show()
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
fun CategoryList(
    viewModel: NewsListViewModel,
    list: List<String>,
    category: MutableState<String>,
    selectedIndex: MutableState<Int>
) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        state.animateScrollToItem(selectedIndex.value)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally),
            text = "Categories",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        LazyRow(state = state) {
            itemsIndexed(items = list) { index, item ->
                OutlinedButton(
                    onClick = {
                        category.value = item
                        selectedIndex.value = index
                        coroutineScope.launch {
                            viewModel.userIntent.send(MainIntent.GetNews(category = item))
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (category.value == item) Color.DarkGray else Color.LightGray,
                        contentColor = if (category.value == item) Color.White else Color.DarkGray
                    ),
                    border = BorderStroke(1.dp, Color.DarkGray),
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = item.capitalize())
                }
            }
        }
    }
}

@Composable
fun NewsList(news: List<Articles>, navContorller: NavController, viewModel: NewsListViewModel) {
    Column() {
        Text(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally),
            text = "News List",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(modifier = Modifier.padding(top = 2.dp)) {
            items(items = news) { item ->
                /*
                NewsItem(item) {
                    val encodedUrl = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                    navContorller.navigate(MainScreen.NewsDetail.route + "/" + encodedUrl)
                }
                Divider(
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
                 */
                NewsCardItem(news = item, viewModel = viewModel) {
                    val encodedUrl = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                    navContorller.navigate(MainScreen.NewsDetail.route + "/" + encodedUrl)
                }
            }
        }
    }
}

@Composable
fun NewsItem(news: Articles, onClick: (url: String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick.invoke(news.url ?: "https://www.google.com") }
    ) {
        val url = news.url

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            news.title?.let {
                Text(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    text = it,
                    fontWeight = FontWeight.Bold
                )
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

@Composable
fun NewsCardItem(news: Articles, viewModel: NewsListViewModel, onClick: (url: String) -> Unit) {
    val isButtonClicked = remember { mutableStateOf(false) }
    var buttonText = remember { mutableStateOf("Fav") }
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
                    Spacer(modifier = Modifier)
                    OutlinedButton(onClick = {
                        if (!isButtonClicked.value) {
                            viewModel.insertArticles(news)
                            buttonText.value = "Faved"
                        }
                    }) {
                        Text(text = buttonText.value)
                    }
                }
            }

        }
    }
}