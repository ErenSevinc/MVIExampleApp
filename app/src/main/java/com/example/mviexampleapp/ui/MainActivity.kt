package com.example.mviexampleapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mviexampleapp.ui.component.MainNavigation
import com.example.mviexampleapp.ui.component.MainScreen
import com.example.mviexampleapp.ui.theme.MVIExampleAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val newsListViewModel: MainViewModel by viewModels()
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
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
                                        IconButton(onClick = { navController.navigateUp() }) {
                                            Icon(
                                                Icons.Filled.ArrowBack,
                                                contentDescription = "Back Icon"
                                            )
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.LightGray),
                                actions = {
                                    IconButton(onClick = { navController.navigate(MainScreen.Favourites.route) }) {
                                        Icon(
                                            Icons.Outlined.BookmarkBorder,
                                            contentDescription = "Fav Icon"
                                        )
                                    }
                                }
                            )
                        }
                    ) {
                        MainNavigation(navController = navController, toolbarTitle = toolbarTitle, paddingValues = it)
                    }
                }
            }
        }
    }
}