package com.example.mviexampleapp.utils

sealed class Resource<out T> {
    object Loading: Resource<Nothing>()
    data class Error(val errorMessage: String?): Resource<Nothing>()
    data class Success<out T>(val data: T?): Resource<T>()
}