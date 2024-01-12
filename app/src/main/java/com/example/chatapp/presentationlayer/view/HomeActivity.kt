package com.example.chatapp.presentationlayer.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.utils.SharedPrefs
import com.example.chatapp.presentationlayer.adapter.OtherUserAdapter
import com.example.chatapp.databinding.ActivityHomeBinding
import com.example.chatapp.presentationlayer.viewmodel.HomePageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val homePageViewModel: HomePageViewModel by viewModels()
    private var myAdapter: OtherUserAdapter = OtherUserAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runBlocking {
            val list = SharedPrefs.setUserCredential?.let { homePageViewModel.getOtherUsers() }
            Log.d("TAG666", "HomeActivity: $list")
            binding.otherUserRV.apply {
                layoutManager = LinearLayoutManager(this@HomeActivity)
                adapter = myAdapter
                if (list != null) {
                    myAdapter.setData(list)
                }
            }
        }


    }
}