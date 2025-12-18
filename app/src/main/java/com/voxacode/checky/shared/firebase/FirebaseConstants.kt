package com.voxacode.checky.shared.firebase

object FirebaseConfig {
    const val DATABASE_VERSION = 1
}

object FirebaseKeys {
    const val GAMES = "games"
    const val PLAYERS = "players"
    const val WHITE = "white"
    const val BLACK = "black"
    const val TIME_CONTROL = "timeControl"
    const val GAME_STATUS = "status"
    const val MOVES = "moves"
    const val DATABASE_VERSION = "version"
    const val PLAYER_UID = "uid"
    const val PLAYER_NAME = "name"
    const val PLAYER_ACTIVE = "active"
}

object GameStatus {
    const val WAITING = "waiting"
    const val ONGOING = "ongoing"
    const val ENDED = "ended"
}