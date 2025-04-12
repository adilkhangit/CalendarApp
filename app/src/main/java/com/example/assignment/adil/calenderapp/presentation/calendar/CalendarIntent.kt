package com.example.assignment.adil.calenderapp.presentation.calendar

import com.example.assignment.adil.calenderapp.data.api.TaskModel
import java.time.YearMonth

sealed class CalendarIntent {
    data class LoadTasks(val userId: Int) : CalendarIntent()
    data class ChangeMonth(val yearMonth: YearMonth) : CalendarIntent()
    data class AddTask(val userId: Int, val task: TaskModel) : CalendarIntent()
    data class DeleteTask(val userId: Int, val taskId: Int) : CalendarIntent()
    object DismissError : CalendarIntent()
    data class SyncTasks(val userId: Int) : CalendarIntent()
} 