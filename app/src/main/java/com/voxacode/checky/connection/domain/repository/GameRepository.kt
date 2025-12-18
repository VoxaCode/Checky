package com.voxacode.checky.connection.domain.repository

import kotlinx.coroutines.flow.Flow
import com.voxacode.checky.connection.domain.model.GamePreferences

interface GameRepository {
    suspend fun createGame(gamePref: GamePreferences): String 
    suspend fun waitUntilPlayerJoins(code: String): Unit
    suspend fun joinGame(code: String): Unit 
    suspend fun deleteGame(code: String): Unit
}
