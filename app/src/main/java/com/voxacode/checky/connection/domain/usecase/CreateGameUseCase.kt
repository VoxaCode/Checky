package com.voxacode.checky.connection.domain.usecase

import javax.inject.Inject 
import com.voxacode.checky.connection.domain.repository.GameRepository
import com.voxacode.checky.connection.domain.model.GamePreferences

class CreateGameUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(
        gamePref: GamePreferences
    ): String {
        return gameRepository.createGame(
            gamePref = gamePref
        )
    }
}
