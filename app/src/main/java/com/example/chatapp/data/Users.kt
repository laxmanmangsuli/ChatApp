package com.example.chatapp.data

data class Users(
    val username: String,
    val password: String,
    val userid: String,
    val email: String,
    var count :Long = 0
)
