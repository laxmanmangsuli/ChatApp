package com.example.chatapp.presentationlayer.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.data.User
import com.example.chatapp.domain.repository.UsersRepository
import com.example.chatapp.presentationlayer.adapter.OtherUserAdapter
import com.example.chatapp.presentationlayer.viewmodel.HomePageViewModel
import com.example.chatapp.utils.Resource
import com.example.chatapp.utils.SharedPrefs
import com.example.chatapps.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val homePageViewModel: HomePageViewModel by viewModels()
    private var myAdapter: OtherUserAdapter = OtherUserAdapter(this)
    @Inject
    lateinit var usersRepository: UsersRepository
    private var userList: List<User> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUsers()
        logout()
        setupSearchView()
        currentUser()
        getLastMessage()

    }
    private fun getLastMessage() {
        val userIds = userList.map { it.userid }
        userIds.forEach { userId ->
            lifecycleScope.launch {
                usersRepository.getLastMessageAndUnreadCount(userId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val data = resource.data
                            val user = userList.find { it.userid == userId }
                            myAdapter.setLastMessage(user!!, data.lastMessage)
                            myAdapter.setUnreadMessageCount(user.userid, data.unreadCount, data.lastMessage)
                        }
                        is Resource.Loading -> {
                        }
                        is Resource.Error -> {
                        }
                    }
                }
            }
        }
    }


    private fun currentUser() {
        val currentUsername =SharedPrefs.setUserCredentialUserName
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

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}