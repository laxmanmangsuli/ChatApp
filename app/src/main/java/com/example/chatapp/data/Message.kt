package com.example.chatapp.data

data class Message(
    var readMessage :Boolean = false,
    val type: String ="",
    val imageUri: String,
    val content: String = "",
    val time: Long = 0,
    val messageId: String = "",
    val currentUser: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val chatId: String = "",
)
{
    constructor() : this(false,"", "", "", 0, "", "", "", "", "")
}
