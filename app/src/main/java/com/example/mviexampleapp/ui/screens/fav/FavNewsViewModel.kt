package com.example.mviexampleapp.ui.screens.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviexampleapp.db.ArticlesRepository
import com.example.mviexampleapp.network.ApiRepository
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
    private val apiRepository: ApiRepository,
    private val articlesRepository: ArticlesRepository
) : ViewModel() {

    val userIntent = Channel<MainIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val state: StateFlow<MainState> = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.GetNews -> {

                    }

                    is MainIntent.GetFavNews -> {
                        getFavArticles()
                    }
                }
            }
        }
    }

    private fun getFavArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = MainState.Loading
            val list = articlesRepository.getFavArticles()

            if (list.isNotEmpty()) {
                _state.value = MainState.News(list)
            } else {
                _state.value = MainState.Error("You have not favourites news")
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        userIntent.close()
    }

}