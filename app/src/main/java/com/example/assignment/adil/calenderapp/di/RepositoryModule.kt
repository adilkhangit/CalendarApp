package com.example.assignment.adil.calenderapp.di

import com.example.assignment.adil.calenderapp.data.api.CalendarApiService
import com.example.assignment.adil.calenderapp.data.local.TaskDao
import com.example.assignment.adil.calenderapp.data.repository.CalendarRepositoryImpl
import com.example.assignment.adil.calenderapp.domain.repository.CalendarRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCalendarRepository(
        repository: CalendarRepositoryImpl
    ): CalendarRepository
} 