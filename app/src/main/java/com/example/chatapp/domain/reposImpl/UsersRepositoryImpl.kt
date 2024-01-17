package com.example.chatapp.domain.reposImpl

import android.util.Log
import com.example.chatapp.utils.Constant
import com.example.chatapp.utils.SharedPrefs
import com.example.chatapp.data.Message
import com.example.chatapp.data.User
import com.example.chatapp.domain.repository.UsersRepository
import com.example.chatapp.utils.Resource
import com.example.chatapp.utils.getChatIdFromSenderAndReceiver
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(private val firebaseFireStore: FirebaseFirestore) :
    UsersRepository {
    override suspend fun getOtherUsers(): MutableList<User> = withContext(Dispatchers.IO) {
        val userList = mutableListOf<User>()
        try {
            val querySnapshot = firebaseFireStore.collection("Users").get().await()
            for (document in querySnapshot) {
                val username = document.get("username").toString()
                val userId = document.get("userid").toString()
                if (userId != SharedPrefs.setUserCredential) {
                    userList += User(username = username, userid = userId)
                }
            }
        } catch (exception: Exception) {
            Log.e("TAG555", "Error getting users", exception)
        }

        return@withContext userList
    }

    override suspend fun sendMessage(message: Message) {
        firebaseFireStore
            .collection(Constant.CHAT_COLLECTION)
            .document(message.chatId)
            .collection(message.chatId)
            .add(message)
            .addOnSuccessListener {
                Log.e("TAG111", "sendMessage: message saved")
            }
            .addOnFailureListener {
                Log.e("TAG111", "sendMessage: message failed to save")
            }
    }

    override suspend fun getMessage(receiverId: String): Flow<Resource<List<Message?>>> =
        callbackFlow {
            val senderId = SharedPrefs.setUserCredential ?: ""
            val chatId = getChatIdFromSenderAndReceiver(senderId, receiverId)
            firebaseFireStore
                .collection(Constant.CHAT_COLLECTION)
                .document(chatId)
                .collection(chatId)
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener { value, _ ->
                    Log.e("TAG111", "${value?.documents}")
                    val list = value?.documents?.map { documentSnapshot ->
                        documentSnapshot.toObject(Message::class.java)
                    } ?: emptyList()
                    trySend(Resource.Success(list))
                }
            awaitClose()
        }

}