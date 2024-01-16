package com.example.chatapp.domain.reposImpl

import android.content.Intent
import com.example.chatapp.utils.InjectorUtil
import com.example.chatapp.utils.SharedPrefs
import com.example.chatapp.data.Users
import com.example.chatapp.domain.repository.AuthRepository
import com.example.chatapp.utils.Resource
import com.example.chatapp.presentationlayer.view.HomeActivity
import com.example.chatapp.presentationlayer.view.LoginActivity
import com.example.chatapp.presentationlayer.view.SignupActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseFireStore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
    )
    : AuthRepository {
    override fun getUserId(): String {
        return firebaseFireStore.collection("Users").document().id
    }

    override fun createUser(user: Users, signupActivity: SignupActivity): Flow<Resource<Boolean>> = callbackFlow {
        try {
            trySend(Resource.Loading)
            firebaseFireStore.collection("Users").add(user)
                .addOnSuccessListener {
                    trySend(Resource.Success(true))
                    signupActivity.startActivity(Intent(signupActivity,LoginActivity::class.java))
                    signupActivity.finish()
                    close()
                }
                .addOnFailureListener { e ->
                    trySend(Resource.Error("User Profile Creation Failed due to ${e.localizedMessage}"))
                    close()
                }
        } catch (e: Exception) {
            trySend(Resource.Error("User Profile Creation Failed due to ${e.localizedMessage}"))
            close()
        }
        awaitClose()
    }

    override fun sendEmailVerification(email: String,password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    sentVerificationLink()
                }
            }
    }

    private fun sentVerificationLink() {
        val user: FirebaseUser?= firebaseAuth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener {
                if (it.isSuccessful){
                    InjectorUtil.showToast("Verification email sent.")
                }else{
                    InjectorUtil.showToast("Failed to send verification email.")
                }
            }
    }

    override suspend fun loginUser(username: String, password: String, activity: LoginActivity) {
        try {
            val querySnapshot = firebaseFireStore.collection("Users")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                val emailQuerySnapshot = firebaseFireStore.collection("Users")
                    .whereEqualTo("email", username)
                    .whereEqualTo("password", password)
                    .get()
                    .await()

                if (!emailQuerySnapshot.isEmpty) {
                    val userDoc = emailQuerySnapshot.documents.first()
//                    val email = userDoc.getString("email")
                    val id = userDoc.getString("userid")
                    firebaseAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener { authResult ->
                            if (authResult.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                if (user != null && user.isEmailVerified) {
                                    InjectorUtil.showToast("Login successful.")
                                    SharedPrefs.isUserLogin = true
                                    SharedPrefs.setUserCredential = id
                                    val intent = Intent(activity, HomeActivity::class.java)
                                    activity.startActivity(intent)
                                    activity.finish()
                                } else {
                                    InjectorUtil.showToast("Please verify your email.")
                                }
                            } else {
                                InjectorUtil.showToast("Authentication failed.")
                            }
                        }
                } else {
                    InjectorUtil.showToast("Username or email not found.")
                }
            } else {
                val userDoc = querySnapshot.documents.first()
                val email = userDoc.getString("email")
                val id = userDoc.getString("userid")
                if (email != null) {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { authResult ->
                            if (authResult.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                if (user != null && user.isEmailVerified) {
                                    InjectorUtil.showToast("Login successful.")
                                    SharedPrefs.isUserLogin = true
                                    SharedPrefs.setUserCredential = id
                                    val intent = Intent(activity, HomeActivity::class.java)
                                    activity.startActivity(intent)
                                    activity.finish()
                                } else {
                                    InjectorUtil.showToast("Please verify your email.")
                                }
                            } else {
                                InjectorUtil.showToast("Authentication failed.")
                            }
                        }
                }
            }
        } catch (e: Exception) {
            InjectorUtil.showToast("An error occurred.")
        }
    }


}