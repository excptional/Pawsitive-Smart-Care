package com.by.pawsitive.db
sealed class Response<out T> {
    data class Success<out T>(val data: String) : Response<T>()
    data class Failure(val message: String) : Response<Nothing>()
    object Loading : Response<Nothing>()
}
