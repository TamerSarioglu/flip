package com.tamerthedark.flip.data.mapper

import com.tamerthedark.flip.data.local.entity.GameScoreEntity
import com.tamerthedark.flip.domain.model.GameScore

fun GameScoreEntity.toDomain(): GameScore {
    return GameScore(
        id = id,
        score = score,
        difficulty = difficulty,
        timestamp = timestamp
    )
}

fun GameScore.toEntity(): GameScoreEntity {
    return GameScoreEntity(
        id = id,
        score = score,
        difficulty = difficulty,
        timestamp = timestamp
    )
} 