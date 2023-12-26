package com.example.mviexampleapp.ui.component.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.screens.list.NewsListViewModel
import kotlinx.coroutines.launch

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