package com.tamerthedark.flip.domain.model

sealed class DialogType {
    object None : DialogType()
    object Difficulty : DialogType()
    object Retry : DialogType()
    object Win : DialogType()
} 