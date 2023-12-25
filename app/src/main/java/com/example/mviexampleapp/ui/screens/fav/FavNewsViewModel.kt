package com.example.mviexampleapp.ui.screens.fav

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviexampleapp.db.ArticlesRepository
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.network.ApiRepository
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.component.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavNewsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()
    private val _isSearch = MutableStateFlow(false)
    val isSearch = _isSearch.asStateFlow()
    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _favNews = MutableStateFlow<List<Articles>?>(null)
    val favNews = searchText
        .combine(_favNews) {text, articles ->
            if (text.isBlank()) {
                articles
            } else {
                delay(2000L)
                articles?.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .onEach { _isSearch.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _favNews.value
        )

    init {
        getFavArticles()
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        Log.d("RESULT", _favNews.value.toString())
    }


    fun getFavArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            _isSearch.value = true
            val list = mutableListOf<Articles>()
            if (articlesRepository.getFavArticles().isNotEmpty()) {
                articlesRepository.getFavArticles().forEach { item ->
                    item.isFavourite = true
                    list.add(item)
                }
                _isSearch.value = false
                _favNews.value = list.toList()
                list.clear()
            } else {
                _isSearch.value = false
                _errorMessage.value = "You have not favourites news"
            }
        }
    }
}