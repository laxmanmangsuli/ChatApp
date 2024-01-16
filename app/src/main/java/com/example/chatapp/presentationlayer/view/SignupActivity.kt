package com.example.chatapp.presentationlayer.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.presentationlayer.viewmodel.SignUpViewModel
import com.example.chatapp.utils.Constant
import com.example.chatapp.utils.InjectorUtil
import com.example.chatapps.databinding.ActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signUpViewModel : SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signup()

        binding.loginPageBTN.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun signup() {
        binding.signupBTN.setOnClickListener {
            if (showError()){
                if (Constant.isValidEmail(binding.crEmail.text.toString())){
                    if (Constant.isPasswordValid(password = binding.crPasswordET.text.toString())){
                        if (binding.crPasswordET.text.toString() ==  binding.crConfirmPasswordET.text.toString()){
                            signUpViewModel.createUserProfile(binding.crUsernameET.text.toString(), binding.crPasswordET.text.toString(),binding.crEmail.text.toString(),this)

                        }else{
                            InjectorUtil.showToast("Passwords do not match")
                        }
                    }else{
                        InjectorUtil.showToast("Password must contain at least 8 characters, 1 capital letter, and 1 symbol")
                    }
                }else{
                    InjectorUtil.showToast("Email not valid")
                }
            }
        }
    }

    private fun showError() :Boolean{
        if (binding.crUsernameET.text.isEmpty()){
            binding.crUsernameET.error = "Please Enter Username"
            return false
        }else if (binding.crEmail.text.isEmpty()){
            binding.crEmail.error = "Please Enter Email"
            return false
        }else if (binding.crPasswordET.text?.isEmpty() == true){
            binding.crPasswordET.error = "Please Enter Password"
            return false
        }else if(binding.crConfirmPasswordET.text?.isEmpty() == true){
            binding.crConfirmPasswordET.error = "Please Enter Password"
            return false
        }
        return true
    }


}