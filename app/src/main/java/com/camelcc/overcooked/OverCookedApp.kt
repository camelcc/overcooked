package com.camelcc.overcooked

import android.app.Application
import androidx.work.*
import java.time.Duration

class OverCookedApp : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.VERBOSE)
            .build()

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    override fun onCreate() {
        super.onCreate()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWork>(
            Duration.ofMinutes(60))
            .setConstraints(constraints)
            .addTag("SyncWorkTag")
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "Sync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest)
    }
}