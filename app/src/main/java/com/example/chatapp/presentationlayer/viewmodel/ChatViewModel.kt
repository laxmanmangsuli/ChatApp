package com.example.chatapp.presentationlayer.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.Message
import com.example.chatapp.domain.repository.UsersRepository
import com.example.chatapp.presentationlayer.view.ChatActivity
import com.example.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val usersRepository: UsersRepository) :
    ViewModel() {

    private val _messageReadStatus = MutableStateFlow<Resource<Boolean>>(Resource.Loading)

    private var _messages = MutableStateFlow<List<Message?>>(emptyList())
    val messages: StateFlow<List<Message?>> = _messages

    fun markMessageAsRead(receiverId: String) {
        viewModelScope.launch {
            try {
                _messageReadStatus.value = Resource.Loading
                usersRepository.markMessageAsRead(receiverId)
                    .collect {
                        _messageReadStatus.value = it
                    }
            } catch (e: Exception) {
                _messageReadStatus.value = Resource.Error("Error marking messages as read")
            }
        }
    }
    suspend fun sendMessage(message: Message) {
        viewModelScope.launch {
            usersRepository.sendMessage(message = message)
        }
    }




    suspend fun getAllMessages(receiverId: String, chatActivity: ChatActivity) = viewModelScope.launch {
        usersRepository.getMessage(receiverId).collectLatest {
            when (it) {
                is Resource.Success -> {
                    _messages.value = it.data
                    chatActivity.binding.progressBar.visibility = View.GONE
                }

                is Resource.Loading -> {
                    chatActivity.binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Error -> {
                    chatActivity.binding.progressBar.visibility = View.GONE
                }
            }
        }
    }


}