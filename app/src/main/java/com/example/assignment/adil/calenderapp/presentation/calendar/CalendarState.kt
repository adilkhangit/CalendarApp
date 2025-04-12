package com.example.assignment.adil.calenderapp.presentation.calendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.assignment.adil.calenderapp.data.api.TaskDetail
import java.time.YearMonth

data class CalendarState(
    val tasks: List<TaskDetail> = emptyList(),
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val lastSyncTime: Long = 0L,
    val error: String? = null
) 