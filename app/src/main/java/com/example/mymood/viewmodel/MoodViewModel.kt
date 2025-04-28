package com.example.mymood.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymood.data.MoodRepository
import com.example.mymood.model.MoodEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class MoodViewModel(private val repository: MoodRepository) : ViewModel() {

    val moodEntries: Flow<List<MoodEntry>> = repository.getAllMoods()

    fun addMood(mood: String, notes: String?, sleepHours: String, stressLevel: Int) {
        viewModelScope.launch {
            val moodEntry = MoodEntry(
                mood = mood,
                notes = notes,
                sleepHours = sleepHours,
                stressLevel = stressLevel,
                timestamp = System.currentTimeMillis()
            )
            repository.insertMood(moodEntry)
        }
    }
}