package com.example.assignment.adil.calenderapp.di

import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {
    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(): Configuration.Provider {
        return object : Configuration.Provider {
            override val workManagerConfiguration: Configuration
                get() = Configuration.Builder()
                    .build()
        }
    }
} 