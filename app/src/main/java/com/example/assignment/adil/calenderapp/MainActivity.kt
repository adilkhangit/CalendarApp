package com.example.assignment.adil.calenderapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.assignment.adil.calenderapp.presentation.calendar.CalendarScreen
import com.example.assignment.adil.calenderapp.presentation.splash.SplashScreen
import com.example.assignment.adil.calenderapp.ui.theme.CalenderAppTheme
import com.example.assignment.adil.calenderapp.worker.SyncManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var syncManager: SyncManager

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalenderAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isSynced by remember { mutableStateOf(false) }
                    var syncFailed by remember { mutableStateOf(false) }
                    var workId by remember { mutableStateOf<WorkRequest?>(null) }
                    var syncProgress by remember { mutableStateOf(0f) }

                    LaunchedEffect(Unit) {
                        val userId = 1
                        val request = syncManager.startSync(
                            userId = userId,
                            lifecycleOwner = this@MainActivity,
                            onSuccess = { 
                                syncProgress = 1f
                                isSynced = true 
                            },
                            onFailure = {
                                syncFailed = true 
                            }
                        )
                        workId = request
                    }

                    if (isSynced || syncFailed) {
                        CalendarScreen()
                    } else {
                        SplashScreen(
                            onCancelSync = {
                                workId?.let { request ->
                                    WorkManager.getInstance(this).cancelWorkById(request.id)
                                    syncFailed = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

