package com.example.assignment.adil.calenderapp.di

import android.content.Context
import com.example.assignment.adil.calenderapp.data.local.CalendarDatabase
import com.example.assignment.adil.calenderapp.data.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarModule {

    @Provides
    @Singleton
    fun provideCalendarDatabase(
        @ApplicationContext context: Context
    ): CalendarDatabase {
        return CalendarDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: CalendarDatabase): TaskDao {
        return database.taskDao()
    }

} 