package com.yamin.chatchat.data.models

data class Chats(
    var userId: String,
    var userFirstName: String,
    var userLastName: String,
    var userProfileImage: String,
    var lastMessage: Message
)
