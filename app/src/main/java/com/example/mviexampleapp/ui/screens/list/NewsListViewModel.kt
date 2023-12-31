package com.example.mviexampleapp.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviexampleapp.db.ArticlesRepository
import com.example.mviexampleapp.model.Articles
import com.example.mviexampleapp.network.ApiRepository
import com.example.mviexampleapp.ui.component.intent.NewsListIntent
import com.example.mviexampleapp.ui.component.state.NewsListState
import com.example.mviexampleapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    val userIntent = Channel<NewsListIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<NewsListState>(NewsListState())
    val state: StateFlow<NewsListState> = _state

    private val _favArticles = MutableStateFlow(emptyList<Articles>())


    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is NewsListIntent.GetNews -> {
                        getNews(category = it.category)
                    }

                    is NewsListIntent.GetFavNews -> {
                        getFavArticles()
                    }

                    is NewsListIntent.InsertNews -> {
                        insertArticles(articles = it.articles, category = it.category)
                    }

                    is NewsListIntent.DeleteNews -> {
                        deleteArticles(articles = it.articles, category = it.category.toString())
                    }
                }
            }
        }
    }

    private fun getNews(category: String) {
        apiRepository.getHeadlineNews(category)
            .onEach { result ->
                when (result) {
                    is Resource.Error -> {
                        _state.update { it.copy(errorMessage = result.errorMessage) }
                    }

                    Resource.Loading -> {
                        _state.update { it.copy(loading = true) }
                    }

                    is Resource.Success -> {
                        val stateList = mutableListOf<Articles>()
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
                            _state.update { it.copy(news = stateList.toList(), loading = false) }
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
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