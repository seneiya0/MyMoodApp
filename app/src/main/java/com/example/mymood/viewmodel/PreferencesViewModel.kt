package com.example.mymood.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PreferencesViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _defaultMood = MutableStateFlow(getDefaultMood())
    val defaultMood: StateFlow<String> = _defaultMood


    fun setDefaultMood(mood: String) {
        viewModelScope.launch {
            sharedPreferences.edit().putString("default_mood", mood).apply()
            _defaultMood.value = mood
        }
    }

    private val _defaultSleep = MutableStateFlow(getSleepGoal())
    val defaultSleep: StateFlow<String> = _defaultSleep

    fun setDefaultSleep(hours: String) {
        viewModelScope.launch {
            sharedPreferences.edit().putString("sleep_goal", hours).apply()
            _defaultSleep.value = hours
        }
    }

    private val _reminderTime = MutableStateFlow(getReminderTime())
    val reminderTime: StateFlow<String> = _reminderTime

    fun setReminderTime(time: String) {
        viewModelScope.launch {
            sharedPreferences.edit().putString("reminder_time", time).apply()
            _reminderTime.value = time
        }
    }

    private fun getReminderTime(): String =
        sharedPreferences.getString("reminder_time", "9:00 AM") ?: "9:00 AM"

    private fun getDefaultMood(): String =
        sharedPreferences.getString("default_mood", "😊") ?: "😊"

    private fun getSleepGoal(): String =
        sharedPreferences.getString("sleep_goal", "8 hours") ?: "8 hours"


}
