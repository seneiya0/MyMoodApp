package com.example.mymood.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {
    fun scheduleReminderWorker(context: Context, hour: Int, minute: Int) {
        val currentTime = Calendar.getInstance()

        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(currentTime)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        val delay = targetTime.timeInMillis - currentTime.timeInMillis
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("daily_reminder")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily_reminder_work",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}