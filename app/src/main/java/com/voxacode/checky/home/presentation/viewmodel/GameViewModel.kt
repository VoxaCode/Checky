package com.voxacode.checky.home.presentation.viewmodel

import android.util.Log
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.voxacode.checky.R

import com.voxacode.checky.connection.domain.exception.GameDoesNotExistException
import com.voxacode.checky.connection.domain.exception.DatabaseMismatchException
import com.voxacode.checky.connection.domain.exception.NoResponseException
import com.voxacode.checky.connection.domain.usecase.CreateGameUseCase
import com.voxacode.checky.connection.domain.usecase.WaitUntilPlayerJoinsUseCase
import com.voxacode.checky.connection.domain.usecase.JoinGameUseCase
import com.voxacode.checky.connection.domain.usecase.DeleteGameUseCase
import com.voxacode.checky.connection.domain.model.GamePreferences
import com.voxacode.checky.connection.domain.model.PlayerColor
import com.voxacode.checky.connection.domain.model.TimeControl

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.Job

sealed class GameState {
    object Idle : GameState()
    object Creating : GameState()
    object Deleting : GameState()
    object PlayerJoined : GameState()
    data class Created(val code: String) : GameState()
    open class Error(open val message: String) : GameState()
    data class CreateError(override val message: String) : Error(message)
    data class DeleteError(
        override val message: String, val code: String
    ) : Error(message)
}

sealed class JoinState {
    object Idle : JoinState()
    object Joining : JoinState()
    object Joined : JoinState()
    data class Error(val message: String) : JoinState()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createGameUseCase: CreateGameUseCase,
    private val waitUntilPlayerJoinsUseCase: WaitUntilPlayerJoinsUseCase,
    private val joinGameUseCase: JoinGameUseCase,
    private val deleteGameUseCase: DeleteGameUseCase
) : ViewModel() {

    private var gameJob: Job? = null
    private val _gameState = MutableStateFlow<GameState>(GameState.Idle)
    val gameState = _gameState.asStateFlow()
    
    private val _joinState = MutableStateFlow<JoinState>(JoinState.Idle)
    val joinState = _joinState.asStateFlow()
    
    fun resetGameState() {
        _gameState.value = GameState.Idle
    }
    
    fun resetJoinState() {
        _joinState.value = JoinState.Idle
    }
    
    fun createGame(
        playerColor: PlayerColor,
        timeControl: TimeControl
    ) {
        if(_gameState.value !is GameState.Idle &&
           _gameState.value !is GameState.CreateError) return
              
        val gamePref = GamePreferences(
            playerColor = playerColor,
            timeControl = timeControl
        )
        
        gameJob = viewModelScope.launch {
            try {
                _gameState.value = GameState.Creating
                delay(600)
                val code = createGameUseCase(gamePref)
                _gameState.value = GameState.Created(code)
                waitUntilPlayerJoinsUseCase(code)
                _gameState.value = GameState.PlayerJoined
                
            } catch(ex: CancellationException) {
                throw ex
            } catch(e: Exception) {
                _gameState.value = GameState.CreateError(
                    context.getString(R.string.unexpected_error_generic)
                )
            } 
        }
    }
    
    fun deleteGame() {
        val code = when(val current = _gameState.value) {
            is GameState.Created -> current.code
            is GameState.DeleteError -> current.code
            else -> return
        }
        
        gameJob?.cancel()
        gameJob = null
        
        viewModelScope.launch {
            try {
                _gameState.value = GameState.Deleting
                deleteGameUseCase(code)           
                _gameState.value = GameState.Idle
                
            } catch(ex: CancellationException) {
                throw ex
            } catch(e: Exception) {
                val message = context.getString(R.string.unexpected_error_generic)
                _gameState.value = GameState.DeleteError(
                    message = message,
                    code = code
                )
            }
        }
    }
    
    fun joinGame(code: String) {
        if(_joinState.value !is JoinState.Idle &&
           _joinState.value !is JoinState.Error) return
           
        viewModelScope.launch {
            try {
                _joinState.value = JoinState.Joining
                delay(600)
                joinGameUseCase(code)
                _joinState.value = JoinState.Joined
                
            } catch(e: NoResponseException) {
                val message = context.getString(R.string.join_error_no_response)
                _joinState.value = JoinState.Error(message)
                
            } catch(e: DatabaseMismatchException) {
                val message = context.getString(R.string.join_error_database_version_mismatch)  
                _joinState.value = JoinState.Error(message)
                
            } catch(e: GameDoesNotExistException) {
                val message = context.getString(R.string.join_error_game_not_exist)
                _joinState.value = JoinState.Error(message)
                
            } catch(e: CancellationException) {
                throw e
                
            } catch(e: Exception) {
                Log.e("idjsj" ,"jdjd", e)
                val message = context.getString(R.string.unexpected_error_generic)
                _joinState.value = JoinState.Error(message)
            }
        }     
    }
}
