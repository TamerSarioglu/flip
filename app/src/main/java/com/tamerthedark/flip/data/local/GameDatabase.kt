package com.tamerthedark.flip.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tamerthedark.flip.data.local.entity.GameScoreEntity

@Database(
    entities = [GameScoreEntity::class],
    version = 1
)
abstract class GameDatabase : RoomDatabase() {
    abstract val dao: GameDao
} 