package com.example.chatapp.presentationlayer.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.data.Message
import com.example.chatapp.presentationlayer.adapter.MessageAdapter
import com.example.chatapp.presentationlayer.viewmodel.ChatViewModel
import com.example.chatapp.utils.SharedPrefs
import com.example.chatapp.utils.getChatIdFromSenderAndReceiver
import com.example.chatapps.databinding.ActivityChatBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
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
    private var receiverId = ""
    private val PICK_IMAGE_REQUEST = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        receiverId = intent.getStringExtra("userid").toString()
        val username = intent.getStringExtra("username")
        binding.username.text = username

        getAllMessages(receiverId)
        sendMessage(receiverId)
        back()
        getImage()

    }

    private fun getImage() {
        binding.ivSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

    }

    private fun back() {
        binding.ivBack.setOnClickListener {
            finish()
        }
    }


    private fun sendMessage(receiverId: String?) {
        binding.sendBTN.setOnClickListener {
            val messages = binding.messageET.text.toString()
            if (receiverId != null && messages.isNotEmpty()) {
                lifecycleScope.launch {
                    val message = Message(
                        type = "txt",
                        content = messages,
                        imageUri = "",
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

    private fun getAllMessages(receiverId: String?) {
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
                        (binding.messageRecView.layoutManager as LinearLayoutManager).scrollToPosition(
                            data.size - 1
                        )
                    }
                }.launchIn(lifecycleScope)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                binding.progressbar.visibility = View.VISIBLE
                val imagePath: String = imageUri.toString()
                val fileName: String = getFileName(applicationContext, imageUri)
                val ref: StorageReference = FirebaseStorage.getInstance().reference
                val uploadTask = ref.child("file/$fileName").putFile(imageUri)
                uploadTask.addOnSuccessListener {
                    ref.child("file/$fileName").downloadUrl.addOnSuccessListener { uri ->
                        lifecycleScope.launch {
                            val message = Message(
                                type = "img",
                                content = "",
                                imageUri = "$uri",
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
                            binding.progressbar.visibility = View.GONE
                        }
                        Log.e("TAG", "onActivityResult: $uri")
                    }.addOnFailureListener { exception ->
                        Log.e("TAG", "Download URL retrieval failed: $exception")
                    }
                }.addOnFailureListener { exception ->
                    Log.e("TAG", "Image upload failed: $exception")
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor.use {
            if (cursor != null && cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    return cursor.getString(displayNameIndex)
                }
            }
        }
        return uri.lastPathSegment ?: "unknown"
    }

}
