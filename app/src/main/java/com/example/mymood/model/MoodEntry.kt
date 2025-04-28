package com.example.mymood.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mood: String,
    val notes: String?,
    val sleepHours: String,
    val stressLevel: Int,
    val timestamp: Long
)