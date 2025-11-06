package com.voxacode.checky.auth.data.repository;

import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import com.voxacode.checky.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override fun currentUser(): FirebaseUser? = auth.currentUser
    override suspend fun signInAnonymously(): FirebaseUser? {
        return auth
            .signInAnonymously()
            .await()
            .user
    }
}
