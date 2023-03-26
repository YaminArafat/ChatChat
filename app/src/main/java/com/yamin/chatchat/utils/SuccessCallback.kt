package com.yamin.chatchat.utils

interface SuccessCallback {
    fun onSuccess(operationType: Type)
    fun onFailure(operationType: Type, message: String)
}