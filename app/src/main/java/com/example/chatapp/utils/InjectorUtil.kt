package com.example.chatapp.utils

import android.widget.Toast
import com.example.chatapp.appclass.MyApplication

object InjectorUtil {
    fun showToast(msg:String){
        Toast.makeText(MyApplication.applicationContext(), msg, Toast.LENGTH_SHORT).show()
    }
}