package com.example.mymood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymood.data.MoodRepository
import com.example.mymood.model.MoodEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class MoodViewModel(private val repository: MoodRepository) : ViewModel() {

    val moodEntries: StateFlow<List<MoodEntry>> = repository.allMoods
        .map { moods -> moods.sortedByDescending { it.timestamp } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addMood(mood: String, notes: String?) {
        val newMood = MoodEntry(
            mood = mood,
            notes = notes,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.insertMood(newMood)
        }
    }
}