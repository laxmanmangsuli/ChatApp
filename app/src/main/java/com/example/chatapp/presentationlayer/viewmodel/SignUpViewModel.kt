package com.example.chatapp.presentationlayer.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.utils.InjectorUtil
import com.example.chatapp.data.Users
import com.example.chatapp.domain.repository.AuthRepository
import com.example.chatapp.presentationlayer.view.SignupActivity
import com.example.chatapp.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fireStore: FirebaseFirestore,
) : ViewModel() {

    fun createUserProfile(
        username: String,
        password: String,
        email: String,
        signupActivity: SignupActivity
    ) {
        val model = Users(username, password, authRepository.getUserId(), email)
        fireStore.collection("Users").whereEqualTo("username", username)
            .get().addOnSuccessListener { it ->
                if (it.isEmpty) {
                    viewModelScope.launch {
                        authRepository.createUser(model,signupActivity).collectLatest {
                            when (it) {
                                is Resource.Loading -> {
                                    signupActivity.binding.progressBar.visibility = View.VISIBLE
                                    signupActivity.binding.tvSignup.visibility = View.GONE
                                }

                                is Resource.Error -> {
                                    signupActivity.binding.progressBar.visibility = View.GONE
                                    signupActivity.binding.tvSignup.visibility = View.VISIBLE
                                }

                                is Resource.Success -> {
                                    signupActivity.binding.progressBar.visibility = View.GONE
                                    signupActivity.binding.tvSignup.visibility = View.VISIBLE
                                    sendEmailVarification(email, password)
                                }
                            }
                        }
                        InjectorUtil.showToast("Please Verify Your Mail.")
                    }
                } else {
                    InjectorUtil.showToast("Username Already Exists!")
                }
            }
    }

    private fun sendEmailVarification(email: String, password: String) {
        viewModelScope.launch {
            authRepository.sendEmailVerification(email, password)
        }
    }
}