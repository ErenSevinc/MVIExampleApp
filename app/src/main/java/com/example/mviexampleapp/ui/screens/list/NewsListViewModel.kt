package com.example.mviexampleapp.ui.screens.list

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviexampleapp.network.ApiRepository
import com.example.mviexampleapp.ui.component.MainIntent
import com.example.mviexampleapp.ui.component.MainState
import com.example.mviexampleapp.utils.Constant
import com.example.mviexampleapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    val userIntent = Channel<MainIntent>(Channel.UNLIMITED)
    val state = mutableStateOf<MainState>(MainState.Idle)

    init {
        getNews(category = Constant.CATEGORY_GENERAL)
    }

    private fun handleIntent() {
        viewModelScope.launch {
            userIntent.consumeAsFlow().collect {
                when (it) {
                    is MainIntent.GetNews -> getNews(category = Constant.CATEGORY_GENERAL)
                }
            }
        }
    }

    fun getNews(category: String?) {
        viewModelScope.launch {
            state.value = MainState.Loading
            try {
                when (val result = repository.getHeadlineNews(category = category)) {
                    is Resource.Loading -> {
                        state.value = MainState.Loading
                    }

                    is Resource.Error -> {
                        state.value = MainState.Error(result.errorMessage)
                    }

                    is Resource.Success -> {
                        result.data?.let { response ->
                            state.value = MainState.News(news = response)
                        }
                    }
                }
            } catch (e: Exception) {
                state.value = MainState.Error(error = e.localizedMessage.toString())
            }

        }
    }
}