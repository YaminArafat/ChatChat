package com.yamin.chatchat.data.models

data class User(
    private val uId: String,
    private val email: String,
    private val profileImage: String,
    private val firstName: String,
    private val lastName: String,
    private val mobile: String,
    private val password: String
)