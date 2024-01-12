package com.example.chatapp.presentationlayer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chatapp.data.User
import com.example.chatapp.domain.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(private val usersRepository: UsersRepository): ViewModel() {

    suspend fun getOtherUsers(): MutableList<User> {
        return usersRepository.getOtherUsers().toMutableList()
    }
}