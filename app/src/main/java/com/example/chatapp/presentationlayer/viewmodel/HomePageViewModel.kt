package com.example.chatapp.presentationlayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.Message
import com.example.chatapp.data.User
import com.example.chatapp.domain.repository.UsersRepository
import com.example.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class HomePageViewModel @Inject constructor(private val usersRepository: UsersRepository) :
    ViewModel() {
    suspend fun getOtherUsers(): MutableList<User> = usersRepository.getOtherUsers().toMutableList()

}