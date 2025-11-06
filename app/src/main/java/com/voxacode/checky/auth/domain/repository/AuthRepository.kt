package com.voxacode.checky.auth.domain.repository;

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    fun currentUser(): FirebaseUser?
    suspend fun signInAnonymously(): FirebaseUser?
}
