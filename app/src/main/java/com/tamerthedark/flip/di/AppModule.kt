package com.tamerthedark.flip.di

import android.app.Application
import androidx.room.Room
import com.tamerthedark.flip.data.local.GameDatabase
import com.tamerthedark.flip.data.repository.GameRepositoryImpl
import com.tamerthedark.flip.domain.repository.GameRepository
import com.tamerthedark.flip.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGameDatabase(app: Application): GameDatabase {
        return Room.databaseBuilder(
            app,
            GameDatabase::class.java,
            "game_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGameRepository(db: GameDatabase): GameRepository {
        return GameRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun provideGameUseCases(repository: GameRepository): GameUseCases {
        return GameUseCases(
            getGameState = GetGameStateUseCase(repository),
            saveGameState = SaveGameStateUseCase(repository),
            saveScore = SaveScoreUseCase(repository),
            getScores = GetScoresUseCase(repository)
        )
    }
} 