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


    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.GetNews -> {
                        getNews(category = it.category)
                    }
                    is MainIntent.GetFavNews -> {
                        getFavArticles()
                    }
                    is MainIntent.InsertNews -> {
                        insertArticles(articles = it.articles, category= it.category)
                    }
                    is MainIntent.DeleteNews -> {
                        deleteArticles(articles = it.articles, category= it.category.toString())
                    }
                    else -> {

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
                            response.articles?.let { articles ->
                                articles.forEach { item ->
                                    if (_favArticles.value.isEmpty()) {
                                        item.isFavourite = false
                                    } else {
                                        _favArticles.value.forEach { fav ->
                                            if (fav.url == item.url) {
                                                item.isFavourite = true
                                            }
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

    private fun insertArticles(articles: Articles, category: String) {
        viewModelScope.launch(IO) {
            val filteredArticles = _favArticles.value.firstOrNull {
                it.url == articles.url
            }
            if (filteredArticles == null) {
                articles.isFavourite = true
                articlesRepository.insert(articles)
            }
            getFavArticles()
            getNews(category)
        }
    }

    private fun deleteArticles(articles: Articles, category: String) {
        viewModelScope.launch(IO) {
            _favArticles.value.forEach {
                if (it.url == articles.url) {
                    articlesRepository.delete(it.id)
                }
            }
            getFavArticles()
            getNews(category)
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