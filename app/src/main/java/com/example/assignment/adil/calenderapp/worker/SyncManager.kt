package com.example.assignment.adil.calenderapp.worker

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun startSync(
        userId: Int,
        lifecycleOwner: LifecycleOwner,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ): WorkRequest {
        Log.d("SyncManager", "Starting sync for user: $userId")
        val request = SyncWorker.createWorkRequest(1)
        val workManager = WorkManager.getInstance(context)
        
        workManager.enqueueUniqueWork("SyncTasks", ExistingWorkPolicy.REPLACE, request)
        Log.d("SyncManager", "Work enqueued with ID: ${request.id}")

        workManager.getWorkInfoByIdLiveData(request.id)
            .observe(lifecycleOwner) { workInfo ->
                if (workInfo != null) {
                    Log.d("SyncManager", "Work state changed: ${workInfo.state}")
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            Log.d("SyncManager", "Work succeeded")
                            onSuccess()
                        }
                        WorkInfo.State.FAILED -> {
                            Log.e("SyncManager", "Work failed")
                            onFailure()
                        }
                        WorkInfo.State.CANCELLED -> {
                            Log.d("SyncManager", "Work cancelled")
                            onFailure()
                        }
                        else -> {
                            Log.d("SyncManager", "Work state: ${workInfo.state}")
                        }
                    }
                }
            }

        return request
    }
}