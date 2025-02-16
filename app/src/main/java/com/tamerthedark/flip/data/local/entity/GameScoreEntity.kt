package com.tamerthedark.flip.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_scores")
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val score: Int,
    val difficulty: String,
    val timestamp: Long
) 