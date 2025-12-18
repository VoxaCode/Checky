package com.voxacode.checky.connection.domain.usecase;

import javax.inject.Inject 
import com.voxacode.checky.connection.domain.repository.GameRepository

class JoinGameUseCase @Inject constructor(
    private val gameRepository: GameRepository 
) {
    suspend operator fun invoke(code: String) {
        gameRepository.joinGame(code.lowercase())
    }
}
