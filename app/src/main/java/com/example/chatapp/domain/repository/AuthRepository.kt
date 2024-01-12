package com.example.chatapp.domain.repository

import com.example.chatapp.data.Users
import com.example.chatapp.utils.Resource
import com.example.chatapp.presentationlayer.view.LoginActivity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getUserId(): String
    fun createUser(user: Users): Flow<Resource<Boolean>>
    fun sendEmailVerification(email: String,password: String)
    suspend fun loginUser(username:String, password:String,activity: LoginActivity)
}