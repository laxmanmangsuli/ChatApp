package com.example.chatapp.data

data class User(
    val username: String,
    val userid: String,
    var unreadMessageCount: Int = 0,
    var totalMessages: Int = 0
)
