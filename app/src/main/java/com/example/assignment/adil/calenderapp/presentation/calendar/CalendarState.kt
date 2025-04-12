package com.example.assignment.adil.calenderapp.presentation.calendar

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.assignment.adil.calenderapp.data.api.TaskDetail
import java.time.YearMonth

data class CalendarState  constructor(
    @SuppressLint("NewApi")
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val tasks: List<TaskDetail> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastSyncTime: Long? = null,
    val isSyncing: Boolean = false
) 