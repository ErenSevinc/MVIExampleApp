package com.example.mviexampleapp.ui.screens.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviexampleapp.db.ArticlesRepository
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.component.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavNewsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    val userIntent = Channel<MainIntent>(Channel.UNLIMITED)
    private val _favNewsState = MutableStateFlow<MainState>(MainState.Idle)
    val favNewsState: StateFlow<MainState> = _favNewsState


    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.GetFavNews -> {
                        getFavArticles()
                    }
                    is MainIntent.DeleteNews -> {
                        deleteArticles(articles = it.articles)
                    }
                    is MainIntent.SearchNews -> {
                        searchNews(query = it.query)
                    }
                    else -> {

                    }
                }
            }
        }
    }


    private fun getFavArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            _favNewsState.value = MainState.Loading
            val result = articlesRepository.getFavArticles()
            if (result.isNotEmpty()) {
                _favNewsState.value = MainState.News(news = result)
            } else {
                _favNewsState.value = MainState.Error(error = "You have not Favourites News")
            }
        }
    }

    private fun deleteArticles(articles: Articles) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = articlesRepository.getFavArticles()
            result.forEach {
                if (it.url == articles.url) {
                    articlesRepository.delete(it.id)
                }
            }
            getFavArticles()
        }
    }

    private fun searchNews(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _favNewsState.value = MainState.Loading
            val result = articlesRepository.getFavArticles()
            if (query.isBlank()) {
                _favNewsState.value = MainState.News(result)
            } else {
                _favNewsState.value = MainState.News(result.filter {
                    it.doesMatchSearchQuery(query)
                })
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userIntent.close()
    }
}