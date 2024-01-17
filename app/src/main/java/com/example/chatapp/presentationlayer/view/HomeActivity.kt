package com.example.chatapp.presentationlayer.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.data.User
import com.example.chatapp.utils.SharedPrefs
import com.example.chatapp.presentationlayer.adapter.OtherUserAdapter
import com.example.chatapp.presentationlayer.viewmodel.HomePageViewModel
import com.example.chatapps.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val homePageViewModel: HomePageViewModel by viewModels()
    private var myAdapter: OtherUserAdapter = OtherUserAdapter(this)
    private var userList: List<User> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUsers()
        logout()
        setupSearchView()
        currentUser()

    }

    private fun currentUser() {
        val currentUsername =SharedPrefs.setUserCredentialUserName
        Log.e("TAG", "currentUser:$currentUsername")
        binding.tvUsername.text = currentUsername

    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterUsers(newText)
                return true
            }
        })
    }

    private fun filterUsers(query: String?) {
        if (query.isNullOrBlank()) {
            myAdapter.setData(userList as MutableList<User>)
        } else {
            val filteredList = userList.filter {
                it.username.contains(query, ignoreCase = true) ||
                        it.username.contains(query, ignoreCase = true)
            }
            myAdapter.setData(filteredList as MutableList<User>)
        }
    }


    private fun getUsers() {
        runBlocking {
            userList = SharedPrefs.setUserCredential?.let { homePageViewModel.getOtherUsers() } ?: emptyList()
            Log.d("TAG666", "HomeActivity: $userList")
            binding.otherUserRV.apply {
                layoutManager = LinearLayoutManager(this@HomeActivity)
                adapter = myAdapter
                myAdapter.setData(userList as MutableList<User>)
            }
        }
    }



    private fun logout() {
        binding.ivLogout.setOnClickListener {
            val alertDialogLogout = AlertDialog.Builder(this)
            alertDialogLogout.setTitle("Logout")
            alertDialogLogout.setMessage("Do you want to logout?")
            alertDialogLogout.setPositiveButton("Yes"){_,_ ->
                startActivity(Intent(this,LoginActivity::class.java))
                SharedPrefs.isUserLogin = false
                finish()

            }
            alertDialogLogout.setNegativeButton("No"){_,_ ->

            }
            alertDialogLogout.show()
        }
    }
}