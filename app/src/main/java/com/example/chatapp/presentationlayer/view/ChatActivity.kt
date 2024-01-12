package com.example.chatapp.presentationlayer.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.utils.SharedPrefs
import com.example.chatapp.presentationlayer.adapter.MessageAdapter
import com.example.chatapp.databinding.ActivityChatBinding
import com.example.chatapp.data.Message
import com.example.chatapp.utils.getChatIdFromSenderAndReceiver
import com.example.chatapp.presentationlayer.viewmodel.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private var myAdapter: MessageAdapter = MessageAdapter()
    private val chatViewModel: ChatViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiverId = intent.getStringExtra("userid")
        val username = intent.getStringExtra("username")
        binding.username.text = username

        lifecycleScope.launch {
            if (receiverId != null) {
                chatViewModel.getAllMessages(receiverId)
                chatViewModel.messages.onEach { data ->
                    withContext(Dispatchers.Main) {
                        val sortedMessages = data.sortedBy { it?.time }
                        myAdapter.setData(sortedMessages)
                        binding.messageRecView.adapter = myAdapter
                        binding.messageRecView.layoutManager =
                            LinearLayoutManager(this@ChatActivity)
                    }
                }.launchIn(lifecycleScope)
            }
        }

        binding.sendBTN.setOnClickListener {
            if (receiverId != null) {
                lifecycleScope.launch {
                    val message = Message(
                        content = binding.messageET.text.toString(),
                        time = System.currentTimeMillis(),
                        messageId = "",
                        currentUser = SharedPrefs.setUserCredential ?: "",
                        senderId = SharedPrefs.setUserCredential ?: "",
                        receiverId = receiverId,
                        chatId = getChatIdFromSenderAndReceiver(
                            SharedPrefs.setUserCredential ?: "",
                            receiverId
                        )
                    )
                    chatViewModel.sendMessage(message)
                }
            }
            binding.messageET.text.clear()
        }
    }
}
