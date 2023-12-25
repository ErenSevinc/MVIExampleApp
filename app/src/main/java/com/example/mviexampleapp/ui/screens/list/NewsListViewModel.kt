package com.example.mviexampleapp.ui.screens.list

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviexampleapp.db.ArticlesRepository
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.network.ApiRepository
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.component.MainState
import com.example.mviexampleapp.utils.Constant
import com.example.mviexampleapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    val userIntent = Channel<MainIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val state: StateFlow<MainState> = _state

    private val _favArticles = MutableStateFlow(emptyList<Articles>())
    val favArticles = _favArticles.asStateFlow()


    init {
        getFavArticles()
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.GetNews -> {
                        getNews(category = it.category)
                    }
                }
            }
        }
    }

    private fun getNews(category: String) {
        viewModelScope.launch(IO) {
            _state.value = MainState.Loading
            try {
                when (val result = apiRepository.getHeadlineNews(category = category)) {
                    is Resource.Loading -> {
                        _state.value = MainState.Loading
                    }

                    is Resource.Error -> {
                        _state.value = MainState.Error(result.errorMessage)
                    }

                    is Resource.Success -> {
                        var stateList = mutableListOf<Articles>()
                        result.data?.let { response ->
                            response.articles?.let { articles->
                                articles.forEach { item ->
                                    _favArticles.value.forEach { fav->
                                        if (fav.url == item.url) {
                                            item.isFavourite = true
                                        }
                                    }
                                    stateList.add(item)
                                }
                            }
                            _state.value = MainState.News(news = stateList.toList())
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = MainState.Error(error = e.localizedMessage.toString())
            }

        }
    }

    fun insertArticles(articles: Articles) {
        viewModelScope.launch(IO) {
            if (_favArticles.value.isEmpty()) {
                articlesRepository.insert(articles)
            } else {
                _favArticles.value.forEach {
                    if (it.url != articles.url) {
                        articlesRepository.insert(articles)
                        articles.isFavourite = true
                    }
                }
            }
        }
    }

    fun deleteArticles(articles: Articles) {
        viewModelScope.launch(IO) {
            if (_favArticles.value.isNotEmpty()) {
                articlesRepository.delete(articles)
                articles.isFavourite = false
            } else {
                _favArticles.value.forEach {
                    if (it.url == articles.url) {
                        articlesRepository.delete(articles)
                        articles.isFavourite = false
                    }
                }
            }
        }
    }

    private fun getFavArticles() {
        viewModelScope.launch(IO) {
            _favArticles.value = articlesRepository.getFavArticles()
        }
    }

    override fun onCleared() {
        super.onCleared()
        userIntent.close()
    }
}