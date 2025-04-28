package com.example.mymood.data

import com.example.mymood.model.MoodEntry
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val moodDao: MoodDao) {
    val allMoods: Flow<List<MoodEntry>> = moodDao.getAllMoods()

    suspend fun insertMood(moodEntry: MoodEntry) {
        moodDao.insertMood(moodEntry)
    }
}