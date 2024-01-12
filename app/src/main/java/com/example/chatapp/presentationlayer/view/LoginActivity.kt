package com.example.chatapp.presentationlayer.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.chatapp.databinding.ActivityLoginBinding
import com.example.chatapp.presentationlayer.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBTN.setOnClickListener {
            if (showError()){
                runBlocking {
                    loginViewModel.loginUser(binding.usernameEmailET.text.toString(),binding.passwordET.text.toString(),this@LoginActivity)
                }
            }
        }

        binding.signUpPageBTN.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    private fun showError() :Boolean{
        if (binding.usernameEmailET.text.isEmpty()){
            binding.usernameEmailET.error = "Please Enter Username"
            return false
        }else if (binding.passwordET.text.isEmpty()) {
            binding.passwordET.error = "Please Enter Email"
            return false
        }
        return true
    }
}