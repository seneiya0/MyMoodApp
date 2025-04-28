package com.example.mymood.data

import com.example.mymood.model.MoodEntry
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val moodDao: MoodDao) {

    suspend fun insertMood(moodEntry: MoodEntry) {
        moodDao.insertMood(moodEntry)
    }

    fun getAllMoods(): Flow<List<MoodEntry>> {
        return moodDao.getAllMoods()
    }
}