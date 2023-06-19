package com.example.mviexampleapp.utils

sealed class Resource<T> (val data: T?= null, val errorMessage: String? = null) {
    class Loading<T>: Resource<T>()
    class Error<T>(errorMessage: String?): Resource<T>(errorMessage = errorMessage)
    class Success<T>(data: T?): Resource<T>(data = data)
}