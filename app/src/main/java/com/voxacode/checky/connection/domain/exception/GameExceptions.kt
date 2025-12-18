package com.voxacode.checky.connection.domain.exception

open class GameException(
    message: String? = null, cause: Throwable? = null
) : Exception(message, cause)

open class InvalidStateException : GameException()
class InvalidOpponentInfoException : InvalidStateException()
class GameDoesNotExistException : InvalidStateException()
class ReferenceDoesNotExistException : InvalidStateException()
class NullReferenceValueException : InvalidStateException()
class NoEmptySlotException : InvalidStateException()
class NotInWaitingStateException : InvalidStateException()
class InvalidGameStatusException : InvalidStateException()
class InvalidReferenceException : GameException()
class DatabaseMismatchException : GameException()
class NoResponseException : GameException()