package com.yamin.chatchat.data.models

data class Friend(
    var id: String,
    var firstName: String,
    var lastName: String,
    var profileImage: String,
    var email: String,
    var mobile: String
) {
    constructor() : this("", "", "", "", "", "")
}
