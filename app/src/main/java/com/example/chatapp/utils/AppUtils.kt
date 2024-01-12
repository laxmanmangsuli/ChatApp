package com.example.chatapp.utils

fun getChatIdFromSenderAndReceiver(senderId: String, receiverId: String): String {
    val list = arrayListOf(senderId, receiverId)
    list.sort()
    return list[0] + "_" + list[1]
}