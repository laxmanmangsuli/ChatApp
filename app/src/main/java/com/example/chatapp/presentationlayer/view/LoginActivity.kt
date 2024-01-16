package com.example.chatapp.presentationlayer.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.presentationlayer.viewmodel.LoginViewModel
import com.example.chatapps.databinding.ActivityLoginBinding
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

        login()
        notAccountGoToSignup()

    }

    private fun notAccountGoToSignup() {
        binding.signUpPageBTN.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun login() {
        binding.loginBTN.setOnClickListener {
            if (showError()){
                runBlocking {
                    loginViewModel.loginUser(binding.usernameEmailET.text.toString(),binding.passwordET.text.toString(),this@LoginActivity)
                }
            }
        }
    }

    private fun showError() :Boolean{
        if (binding.usernameEmailET.text.isEmpty()){
            binding.usernameEmailET.error = "Please Enter Username"
            return false
        }else if (binding.passwordET.text!!.isEmpty()) {
            binding.passwordET.error = "Please Enter Email"
            return false
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}