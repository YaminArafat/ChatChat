package com.yamin.chatchat.data.models

data class Participants(
    var userId1: String,
    var userId2: String
) {
    constructor() : this("", "")
}