package com.example.assignment.adil.calenderapp.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import com.example.assignment.adil.calenderapp.domain.repository.CalendarRepository
import com.example.assignment.adil.projectassignment.Utility.NetworkCheck
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerEntryPoint {
        fun repository(): CalendarRepository
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if(!NetworkCheck.isNetworkAvailable(context)) return@withContext Result.failure()
            Log.d(TAG, "Worker started")
            
            val userId = inputData.getLong(KEY_USER_ID, -1)
            Log.d(TAG, "User ID: $userId")
            
            if (userId == -1L) {
                Log.e(TAG, "Invalid user ID")
                return@withContext Result.failure()
            }

            val entryPoint = EntryPointAccessors.fromApplication(context, WorkerEntryPoint::class.java)
            val repository = entryPoint.repository()
            
            Log.d(TAG, "Starting sync operation for user $userId")
            val result = repository.syncTasksToServer(userId.toInt())
            
            if (result.isSuccess) {
                Log.d(TAG, "Sync completed successfully for user $userId")
                Result.success()
            } else {
                val exception = result.exceptionOrNull()
                Log.e(TAG, "Sync failed for user $userId: ${exception?.message}", exception)
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Worker failed with error: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "SyncWorker"
        private const val KEY_USER_ID = "user_id"

        fun createWorkRequest(userId: Long): OneTimeWorkRequest {
            val inputData = Data.Builder()
                .putLong(KEY_USER_ID, userId)
                .build()

            return OneTimeWorkRequest.Builder(SyncWorker::class.java)
                .setInputData(inputData)
                .build()
        }
    }
}