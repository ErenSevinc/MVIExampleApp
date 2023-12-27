package com.example.mviexampleapp.ui.screens.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviexampleapp.db.ArticlesRepository
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.ui.component.intent.FavNewsIntent
import com.example.mviexampleapp.ui.component.state.FavNewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavNewsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    val userIntent = Channel<FavNewsIntent>(Channel.UNLIMITED)
    private val _favNewsState = MutableStateFlow<FavNewsState>(FavNewsState())
    val favNewsState: StateFlow<FavNewsState> = _favNewsState


    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is FavNewsIntent.GetFavNews -> {
                        getFavArticles()
                    }

                    is FavNewsIntent.DeleteNews -> {
                        deleteArticles(articles = it.articles)
                    }

                    is FavNewsIntent.SearchNews -> {
                        searchNews(query = it.query)
                    }
                }
            }
        }
    }


    private fun getFavArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            _favNewsState.update { it.copy(loading = true) }
            delay(1000)
            val result = articlesRepository.getFavArticles()
            if (result.isNotEmpty()) {
                _favNewsState.update { it.copy(news = result, loading = false) }
            } else {
                _favNewsState.update { it.copy(errorMessage = "You have not Favourites News" , loading = false, news = emptyList()) }
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
            _favNewsState.update { it.copy(loading = true) }
            delay(1000)
            val result = articlesRepository.getFavArticles()
            if (query.isBlank()) {
                _favNewsState.update { it.copy(news = result, loading = false) }
            } else {
                _favNewsState.update {
                    it.copy(news = result.filter { articles ->
                        articles.doesMatchSearchQuery(query)
                    }, loading = false)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userIntent.close()
    }
}