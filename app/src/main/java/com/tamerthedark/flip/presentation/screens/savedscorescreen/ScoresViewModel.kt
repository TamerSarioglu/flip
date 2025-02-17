package com.tamerthedark.flip.presentation.screens.savedscorescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamerthedark.flip.domain.model.GameScore
import com.tamerthedark.flip.domain.use_case.GameUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScoresViewModel @Inject constructor(
    private val gameUseCases: GameUseCases
) : ViewModel() {
    private val _scores = MutableStateFlow<List<GameScore>>(emptyList())
    val scores = _scores.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow<String?>(null)
    val selectedDifficulty = _selectedDifficulty.asStateFlow()

    init {
        loadScores()
    }

    private fun loadScores() {
        viewModelScope.launch {
            gameUseCases.getScores(_selectedDifficulty.value).collect { scores ->
                _scores.value = scores
            }
        }
    }

    fun setDifficultyFilter(difficulty: String?) {
        _selectedDifficulty.value = difficulty
        loadScores()
    }
} 