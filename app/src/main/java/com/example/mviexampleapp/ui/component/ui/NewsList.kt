package com.example.mviexampleapp.ui.component.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mviexampleapp.R
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.ui.component.navigation.MainScreen
import com.example.mviexampleapp.utils.toDate
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NewsList(news: List<Articles>, navContorller: NavController, onFavClick: (Articles) -> Unit) {
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
                NewsCardItem(news = item, onClick = {
                    val encodedUrl = URLEncoder.encode(it, StandardCharsets.UTF_8.toString())
                    navContorller.navigate(MainScreen.NewsDetail.route + "/" + encodedUrl)
                }, onFavClick = onFavClick)
            }
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NewsCardItem(news: Articles, onClick: (url: String) -> Unit, onFavClick: (Articles) -> Unit) {
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
                        onFavClick.invoke(news)
                    }) {
                        Text(text = if (news.isFavourite) "Faved" else "Fav")
                    }
                }
            }

        }
    }
}