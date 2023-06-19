package com.example.mviexampleapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.ui.component.MainState
import com.example.mviexampleapp.ui.theme.MVIExampleAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MVIExampleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(mainViewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(vm: MainViewModel ,modifier: Modifier = Modifier) {
    when(val state = vm.state.value) {
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
                NewsList(news = it)
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
fun NewsList(news: List<Articles>) {
    LazyColumn {
        items(items = news) {
            NewsItem(it)
            Divider(color = Color.LightGray, modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
        }
    }
}

@Composable
fun NewsItem(news: Articles) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
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
            news.description?.let {
                Text(text = it)
            }
        }
    }
}