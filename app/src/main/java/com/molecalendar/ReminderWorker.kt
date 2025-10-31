package com.molecalendar

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val moleId = inputData.getLong("MOLE_ID", -1)
        val moleLocation = inputData.getString("MOLE_LOCATION") ?: "Unknown"

        if (moleId == -1L) {
            return Result.failure()
        }

        // Send notification
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.sendNotification(moleId, moleLocation)

        return Result.success()
    }
}
