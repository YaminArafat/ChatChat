package com.yamin.chatchat.utils

sealed class Response<out T> {
    data class Success<out R>(val data: R) : Response<R>()
    object Running : Response<Nothing>()
    data class Error(val errorMessage: String) : Response<Nothing>()
}
