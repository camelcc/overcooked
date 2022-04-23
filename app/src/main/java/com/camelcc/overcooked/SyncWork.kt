package com.camelcc.overcooked

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWork(appContext: Context, workParams: WorkerParameters): CoroutineWorker(appContext, workParams) {
    private fun createForegroundInfo(): ForegroundInfo {
        // 1
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)
        // 2
        val notification = NotificationCompat.Builder(
            applicationContext, "workDownload")
            .setContentTitle("Downloading Your Image")
            .setTicker("Downloading Your Image")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "Cancel Download", intent)
        // 3
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notification, "workDownload")
        }
        return ForegroundInfo(1, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(
        notificationBuilder: NotificationCompat.Builder,
        id: String
    ) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
        val channel = NotificationChannel(
            id,
            "Overcooked",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Overcooked Notifications"
        notificationManager.createNotificationChannel(channel)
    }

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
        val refresh = withContext(Dispatchers.IO) {
            val repository = Repository(appContext = applicationContext)
            repository.refreshPhotos()
        }
        if (!refresh.isSuccessful) {
            return Result.failure()
        }
        return Result.success()
    }
}