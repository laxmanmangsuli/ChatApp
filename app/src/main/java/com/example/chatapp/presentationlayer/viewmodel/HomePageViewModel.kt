package com.example.chatapp.presentationlayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.Message
import com.example.chatapp.data.User
import com.example.chatapp.domain.repository.UsersRepository
import com.example.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class HomePageViewModel @Inject constructor(private val usersRepository: UsersRepository): ViewModel() {


    suspend fun getOtherUsers(): MutableList<User> {
        return usersRepository.getOtherUsers().toMutableList()
    }

    suspend fun getLastMessagesForUsers(userIds: List<String>): Map<String, Message?> = suspendCoroutine { continuation ->
        val results = mutableMapOf<String, Message?>()
        var remainingCount = userIds.size
        for (receiverId in userIds) {
            viewModelScope.launch {
                usersRepository.getMessage(receiverId)
                    .collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                val messageList = resource.data
                                val lastMessage = messageList.firstOrNull()
                                results[receiverId] = lastMessage
                            }
                            is Resource.Loading -> {

                            }
                            is Resource.Error -> {

                            }
                        }
                        remainingCount--
                        if (remainingCount == 0) {
                            continuation.resume(results)
                        }
                    }
            }
        }
    }

    suspend fun getUnreadMessageCountForUser(userId: String): Int = suspendCoroutine { continuation ->
        var unreadMessageCount = 0

        viewModelScope.launch {
            try {
                when (val resource = usersRepository.getMessage(userId).first()) {
                    is Resource.Success -> {
                        val messageList = resource.data
                        unreadMessageCount = messageList.count { !it!!.isRead } // it?.isRead ?: false
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        // Handle error state if needed
                    }
                }

                continuation.resume(unreadMessageCount)
            } catch (e: Exception) {
                continuation.resume(unreadMessageCount)
            }
        }
    }


}