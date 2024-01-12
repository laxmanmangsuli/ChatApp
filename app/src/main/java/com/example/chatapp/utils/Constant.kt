package com.example.chatapp.utils

import android.text.TextUtils
import android.util.Patterns

object Constant {
    fun isPasswordValid(password: String): Boolean {
        val pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\$%^&*()-_+=]).{8,}$".toRegex()
        return password.matches(pattern)
    }
    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    const val CHAT_COLLECTION = "Chats"
}