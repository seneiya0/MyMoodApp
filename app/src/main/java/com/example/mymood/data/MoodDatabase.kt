package com.example.mymood.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mymood.model.MoodEntry

@Database(entities = [MoodEntry::class], version = 2, exportSchema = false)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
}