package com.tamerthedark.flip.data.local

import androidx.room.*
import com.tamerthedark.flip.data.local.entity.GameScoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: GameScoreEntity)

    @Query("SELECT * FROM game_scores ORDER BY score DESC")
    fun getAllScores(): Flow<List<GameScoreEntity>>

    @Query("SELECT * FROM game_scores WHERE difficulty = :difficulty ORDER BY score DESC")
    fun getScoresByDifficulty(difficulty: String): Flow<List<GameScoreEntity>>
} 