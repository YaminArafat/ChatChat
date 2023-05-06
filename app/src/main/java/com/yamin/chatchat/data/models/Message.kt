package com.yamin.chatchat.data.models

data class Message(
    var senderId: String,
    var messageText: String,
    var receiverId: String,
    var messageTimestamp: Long
)
