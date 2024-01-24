package com.example.chatapp.presentationlayer.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.presentationlayer.viewmodel.LoginViewModel
import com.example.chatapp.utils.InjectorUtil
import com.example.chatapps.databinding.ActivityLoginBinding
import com.example.chatapp.utils.isOnline
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(){
    lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.M)
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun login() {
        binding.loginBTN.setOnClickListener {
            if (isOnline(this)){
            if (showError()){
                runBlocking {
                    binding.btnLogin.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    loginViewModel.loginUser(binding.usernameEmailET.text.toString(),binding.passwordET.text.toString(),this@LoginActivity)
                }
            }
        }else{
            InjectorUtil.showToast("Check your  internet connection")

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