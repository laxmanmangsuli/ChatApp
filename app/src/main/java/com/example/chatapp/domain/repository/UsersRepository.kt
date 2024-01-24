package com.example.chatapp.domain.repository

import com.example.chatapp.data.LastMessageAndUnreadCount
import com.example.chatapp.data.Message
import com.example.chatapp.data.User
import com.example.chatapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    suspend fun getOtherUsers(): List<User>
    suspend fun sendMessage(message: Message)
    suspend fun getMessage(receiverId: String): Flow<Resource<List<Message?>>>
    suspend fun markMessageAsRead(receiverId: String) : Flow<Resource<Boolean>>
    suspend fun getLastMessageAndUnreadCount(receiverId: String): Flow<Resource<LastMessageAndUnreadCount>>
}