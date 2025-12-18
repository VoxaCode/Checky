package com.voxacode.checky.connection.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GamePreferences(
    val playerColor: PlayerColor,
    val timeControl: TimeControl
) : Parcelable

@Parcelize
sealed class PlayerColor : Parcelable {
    @Parcelize
    object Black : PlayerColor()
    @Parcelize
    object White : PlayerColor()
}
    
@Parcelize    
sealed class TimeControl(val minutes: Int) : Parcelable {
    @Parcelize
    object OneMinute : TimeControl(1)
    @Parcelize
    object ThreeMinutes : TimeControl(3)
    @Parcelize
    object FiveMinutes : TimeControl(5)
    @Parcelize
    object TenMinutes : TimeControl(10)
    @Parcelize
    object TwentyMinutes : TimeControl(20)
    @Parcelize
    object ThirtyMinutes : TimeControl(30)
}
   