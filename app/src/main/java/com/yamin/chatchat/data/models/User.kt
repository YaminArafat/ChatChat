package com.yamin.chatchat.data.models

data class User(
    var userId: String,
    var email: String,
    var profileImageDownloadUrl: String,
    var firstName: String,
    var lastName: String,
    var mobile: String,
    var password: String
) {
    constructor() : this("", "", "", "", "", "", "")
}