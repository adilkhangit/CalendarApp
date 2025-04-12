package com.example.assignment.adil.calenderapp

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CalenderApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var configurationProvider: Configuration.Provider

    override val workManagerConfiguration: Configuration
        get() = configurationProvider.workManagerConfiguration
} 