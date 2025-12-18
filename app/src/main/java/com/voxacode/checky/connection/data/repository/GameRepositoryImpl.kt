package com.voxacode.checky.connection.data.repository;

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.TimeoutCancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

import com.voxacode.checky.connection.data.model.Player
import com.voxacode.checky.auth.domain.repository.AuthRepository
import com.voxacode.checky.connection.domain.exception.GameException
import com.voxacode.checky.connection.domain.exception.InvalidGameStatusException
import com.voxacode.checky.connection.domain.exception.InvalidOpponentInfoException
import com.voxacode.checky.connection.domain.exception.InvalidReferenceException
import com.voxacode.checky.connection.domain.exception.ReferenceDoesNotExistException
import com.voxacode.checky.connection.domain.exception.NullReferenceValueException
import com.voxacode.checky.connection.domain.exception.NoEmptySlotException
import com.voxacode.checky.connection.domain.exception.NotInWaitingStateException
import com.voxacode.checky.connection.domain.exception.GameDoesNotExistException
import com.voxacode.checky.connection.domain.exception.DatabaseMismatchException
import com.voxacode.checky.connection.domain.exception.NoResponseException
import com.voxacode.checky.connection.domain.repository.GameRepository
import com.voxacode.checky.connection.domain.model.GamePreferences
import com.voxacode.checky.connection.domain.model.PlayerColor
import com.voxacode.checky.connection.domain.model.TimeControl

import com.voxacode.checky.shared.firebase.FirebaseKeys
import com.voxacode.checky.shared.firebase.FirebaseConfig
import com.voxacode.checky.shared.firebase.GameStatus

import com.google.firebase.FirebaseException
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gamesRef: DatabaseReference,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : GameRepository {
    
    private companion object {
        const val STATUS_OBSERVE_TIMEOUT = 10000L
    }
    
    private fun <T> CancellableContinuation<T>.success(value: T) {
         if(this.isActive) this.resume(value)
    }
    
    private fun <T> CancellableContinuation<T>.error(error: Throwable) {
         if(this.isActive) this.resumeWithException(error)
    }
    
    private fun generateGameCode(length: Int = 6) = buildString {
        val chars = ('a'..'z') + ('0'..'9')
        repeat(length) { append(chars.random()) }
    }
    
    private suspend inline fun <reified T> DatabaseReference.getReferenceValue(): T? {
        val snapshot = this.get().await()
        if(snapshot.exists()) return snapshot.getValue(T::class.java)
        else throw ReferenceDoesNotExistException()
    }
    
    private fun Player.isValidPlayer() = when {
        this.uid.isBlank() -> false
        this.name.isBlank() -> false
        !this.active -> false
        else -> true
    }
    
    private fun DatabaseReference.isPlayerReference(): Boolean {
        val path = this.path.toString()
        val regex = Regex(
            """/${FirebaseKeys.GAMES}""" +
            """/[A-Za-z0-9]+""" +
            """/${FirebaseKeys.PLAYERS}""" +
            """/(${FirebaseKeys.WHITE}|${FirebaseKeys.BLACK})"""
        )
        
        return path.matches(regex)
    }
    
    private fun DatabaseReference.isStatusReference(): Boolean {
        val path = this.path.toString()
        val regex = Regex(
            """/${FirebaseKeys.GAMES}""" +
            """/[A-Za-z0-9]+""" +
            """/${FirebaseKeys.GAME_STATUS}"""
        )
        
        return path.matches(regex)
    }
    
    private fun DatabaseReference.isGameReference(): Boolean {
        val path = this.path.toString()
        val regex = Regex(
            """/${FirebaseKeys.GAMES}""" +
            """/[A-Za-z0-9]+""" 
        )
        
        return path.matches(regex)
    }
   
    private suspend fun DatabaseReference.exists(): Boolean {
        val snapshot = this.get().await()
        return snapshot.exists()
    }
    
    private suspend fun String.isWaiting(): Boolean {
        val gameRef = gamesRef.child(this)
        if(!gameRef.isGameReference()) throw InvalidReferenceException()
        else if(!gameRef.exists()) throw GameDoesNotExistException()
    
        val statusRef = gameRef.child(FirebaseKeys.GAME_STATUS)   
        val status: String? = statusRef.getReferenceValue()
        
        if(status == null) throw NullReferenceValueException()
        return status == GameStatus.WAITING
    }
    
    private suspend fun String.isDatabaseVersionCompatible(): Boolean {
        val gameRef = gamesRef.child(this)
        if(!gameRef.isGameReference()) throw InvalidReferenceException()
        else if(!gameRef.exists()) throw GameDoesNotExistException()
    
        val dbVersionRef = gameRef.child(FirebaseKeys.DATABASE_VERSION)
        val dbVersion: Int? = dbVersionRef.getReferenceValue()
        
        if(dbVersion == null) throw NullReferenceValueException()
        else return dbVersion == FirebaseConfig.DATABASE_VERSION
    }
    
    private suspend inline fun <T> firebaseCall(
        crossinline suspendFunction: suspend (FirebaseUser) -> T
    ): T = withContext(Dispatchers.IO) {
        try {
            val user = authRepository.ensureSignedIn()
            suspendFunction(user)
        } catch(e: FirebaseException) {
            throw GameException(
                message = e.message,
                cause = e 
            )      
        }
    }
   
    override suspend fun createGame(gamePref: GamePreferences): String = firebaseCall { 
        var unique = false
        lateinit var code: String
        
        while(!unique) {
            code = generateGameCode()
            unique = !gamesRef.child(code).exists()
        }
            
        val white = gamePref.playerColor is PlayerColor.White
        val player = Player(uid = it.uid)
            
        val game = mapOf(
            FirebaseKeys.DATABASE_VERSION to FirebaseConfig.DATABASE_VERSION,
            FirebaseKeys.TIME_CONTROL to gamePref.timeControl.minutes,
            FirebaseKeys.GAME_STATUS to GameStatus.WAITING,
            FirebaseKeys.PLAYERS to mapOf(
                FirebaseKeys.WHITE to if(white) player else null,
                FirebaseKeys.BLACK to if(!white) player else null
            )
        )
         
        gamesRef.child(code)
            .setValue(game)
            .await()     
        
        val playerKey = when {
            white -> FirebaseKeys.WHITE 
            else -> FirebaseKeys.BLACK
        }
        
        gamesRef.child(code)
            .child(FirebaseKeys.PLAYERS)
            .child(playerKey)
            .child(FirebaseKeys.PLAYER_ACTIVE)
            .onDisconnect()
            .setValue(false)
        
        code       
    }
    
    //only used to delete game nodes which are in 
    //waiting state, deletion on disconnect is handled
    //by setting plauyer active:false on disconnect, the server
    //side will detect it and automatically perform cleanup.
    override suspend fun deleteGame(code: String): Unit = firebaseCall {
        if(!code.isWaiting()) throw NotInWaitingStateException()
        gamesRef.child(code)
            .removeValue()
            .await()
    }
    
    override suspend fun waitUntilPlayerJoins(code: String): Unit = firebaseCall {
        if(!code.isWaiting()) throw NotInWaitingStateException()
        
        val gameRef = gamesRef.child(code)
        val playersRef = gameRef.child(FirebaseKeys.PLAYERS)
        
        val whiteRef = playersRef.child(FirebaseKeys.WHITE)
        val blackRef = playersRef.child(FirebaseKeys.BLACK)
         
        val emptySlot = when {
            !whiteRef.exists() -> whiteRef
            !blackRef.exists() -> blackRef
            else -> throw NoEmptySlotException()
        }
            
        emptySlot.awaitUntilPlayerJoins()    
        gameRef.child(FirebaseKeys.GAME_STATUS)
            .setValue(GameStatus.ONGOING)
            .await()
    }
    
    override suspend fun joinGame(code: String): Unit = firebaseCall { 
    
        val gameRef = gamesRef.child(code)
        val playersRef = gameRef.child(FirebaseKeys.PLAYERS)
        val statusRef = gameRef.child(FirebaseKeys.GAME_STATUS) 
        
        when {
            !code.isDatabaseVersionCompatible() -> throw DatabaseMismatchException()
            !code.isWaiting() -> throw NotInWaitingStateException()
        }
        
        val whiteRef = playersRef.child(FirebaseKeys.WHITE) 
        val blackRef = playersRef.child(FirebaseKeys.BLACK)
          
        val emptySlot = when {
            !whiteRef.exists() -> whiteRef
            !blackRef.exists() -> blackRef
            else -> throw NoEmptySlotException()
        }
        
        emptySlot.writePlayerInfo(
            Player(uid = it.uid)
        )
        
        emptySlot.child(FirebaseKeys.PLAYER_ACTIVE)
            .onDisconnect()
            .setValue(false)
        
        try {
            statusRef.awaitUntilOngoingStatusWithTimeout()
        } catch(e: TimeoutCancellationException) {
            throw NoResponseException()
        } 
    }
    
    private suspend fun DatabaseReference.awaitUntilPlayerJoins() {
        val emptySlot = this@awaitUntilPlayerJoins
        if(!emptySlot.isPlayerReference()) throw InvalidReferenceException()
        else if(emptySlot.exists()) throw NoEmptySlotException()
        
        suspendCancellableCoroutine { cont -> 
            val slotListener = object: ValueEventListener {
                private var firstCallback = true
                  
                override fun onDataChange(snapshot: DataSnapshot) {
                    val opponent = snapshot.getValue(Player::class.java)
                    
                    when {
                        !cont.isActive -> return
                        firstCallback && snapshot.exists() -> {
                            emptySlot.removeEventListener(this)
                            cont.error(NoEmptySlotException())
                        }
                        
                        firstCallback -> firstCallback = false
                        opponent == null -> {
                            emptySlot.removeEventListener(this)
                            cont.error(NullReferenceValueException())
                        }
                        
                        !opponent.isValidPlayer() -> {
                            emptySlot.removeEventListener(this)
                            cont.error(InvalidOpponentInfoException())
                        }
                        
                        else -> {
                            emptySlot.removeEventListener(this)
                            cont.success(Unit)
                        }
                    }
                }
            
                override fun onCancelled(error: DatabaseError) {
                    cont.error(error.toException())
                    emptySlot.removeEventListener(this)
                }
            }
            
            cont.invokeOnCancellation {
                emptySlot.removeEventListener(slotListener)
            } 
                      
            if(cont.isActive) {
                emptySlot.addValueEventListener(slotListener) 
            }
        }
    } 
    
    private suspend fun DatabaseReference.writePlayerInfo(info: Player) {
        val emptySlot = this@writePlayerInfo
        if(!emptySlot.isPlayerReference()) throw InvalidReferenceException()
        else if(emptySlot.exists()) throw NoEmptySlotException()
    
        suspendCancellableCoroutine { cont -> 
            emptySlot.runTransaction(
                object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        if(currentData.value == null) {
                            currentData.value = info
                            return Transaction.success(currentData)
                        } else { 
                            return Transaction.abort() 
                            cont.error(NoEmptySlotException())
                        }
                    }
                    
                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        snapshot: DataSnapshot?
                    ) {
                        if(committed) cont.success(Unit)
                        else if(error != null) cont.error(error.toException())
                    }
                }
            )
        }
    }

    private suspend fun DatabaseReference.awaitUntilOngoingStatusWithTimeout(
        timeoutInMillis: Long = STATUS_OBSERVE_TIMEOUT
    ) = withTimeout(timeoutInMillis) {
        val statusRef = this@awaitUntilOngoingStatusWithTimeout
        statusRef.awaitUntilOngoingStatus()  
    }
    
    private suspend fun DatabaseReference.awaitUntilOngoingStatus() {
        val statusRef = this@awaitUntilOngoingStatus
        if(!statusRef.isStatusReference()) throw InvalidReferenceException()
        
        suspendCancellableCoroutine { cont->
            val statusListener = object: ValueEventListener {
                private var firstCallback = true
                
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(String::class.java)
                
                    when {
                        !cont.isActive -> return
                        !snapshot.exists() -> {
                            statusRef.removeEventListener(this)
                            cont.error(ReferenceDoesNotExistException())
                        }
                    
                        status == null -> {
                            statusRef.removeEventListener(this)
                            cont.error(NullReferenceValueException())
                        }
                    
                        firstCallback && status != GameStatus.WAITING -> {
                            statusRef.removeEventListener(this)
                            cont.error(NotInWaitingStateException())
                        }
                    
                        firstCallback -> firstCallback = false
                        status == GameStatus.ONGOING -> {
                            statusRef.removeEventListener(this)
                            cont.success(Unit)
                        }
                    
                        else -> {
                            statusRef.removeEventListener(this)
                            cont.error(InvalidGameStatusException())
                        }
                    }
                }
            
                override fun onCancelled(error: DatabaseError) {
                    statusRef.removeEventListener(this)
                    cont.error(error.toException())
                }
            }
            
            cont.invokeOnCancellation {
                statusRef.removeEventListener(statusListener)
            } 
                
            if(cont.isActive) {
                statusRef.addValueEventListener(statusListener)
            }
        }
    }
}