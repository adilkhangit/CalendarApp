package com.example.assignment.adil.calenderapp.di

import android.content.Context
import com.example.assignment.adil.calenderapp.domain.repository.CalendarRepository
import com.example.assignment.adil.calenderapp.worker.SyncManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {
    @Provides
    @Singleton
    fun provideSyncManager(
        @ApplicationContext context: Context,
        repository: CalendarRepository
    ): SyncManager {
        return SyncManager(context)
    }
}