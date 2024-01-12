package com.example.chatapp.domain.di

import com.example.chatapp.domain.reposImpl.AuthRepositoryImpl
import com.example.chatapp.domain.reposImpl.UsersRepositoryImpl
import com.example.chatapp.domain.repository.AuthRepository
import com.example.chatapp.domain.repository.UsersRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideUserRepository(firebaseFireStore: FirebaseFirestore): UsersRepository =
        UsersRepositoryImpl(firebaseFireStore)

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseFireStore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): AuthRepository = AuthRepositoryImpl(firebaseFireStore, firebaseAuth)

}