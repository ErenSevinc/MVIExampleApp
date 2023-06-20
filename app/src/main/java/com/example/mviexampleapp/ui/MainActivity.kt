package com.example.mviexampleapp.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.ui.component.MainNavigation
import com.example.mviexampleapp.ui.component.MainScreen
import com.example.mviexampleapp.ui.component.MainState
import com.example.mviexampleapp.ui.screens.list.NewsListViewModel
import com.example.mviexampleapp.ui.theme.MVIExampleAppTheme
import com.example.mviexampleapp.utils.toDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: NewsListViewModel by viewModels()
    lateinit var navContorller: NavHostController

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navContorller = rememberNavController()
            val navBackStackEntry by navContorller.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: ""
            val toolbarTitle = remember { mutableStateOf("") }

            MVIExampleAppTheme {
                Surface {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(text = toolbarTitle.value, textAlign = TextAlign.Right)
                                },
                                navigationIcon = {
                                    if (currentRoute.isNotEmpty() && currentRoute != MainScreen.NewsList.route) {
                                        IconButton(onClick = { navContorller.navigateUp() }) {
                                            Icon(
                                                Icons.Filled.ArrowBack,
                                                contentDescription = "Back Icon"
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.LightGray)
                            )
                        }
                    ) {
                        MainNavigation(navController = navContorller, toolbarTitle = toolbarTitle, vm = viewModel)
                    }
                }
            }
        }
    }
}