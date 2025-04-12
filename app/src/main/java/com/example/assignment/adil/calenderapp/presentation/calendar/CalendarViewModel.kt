package com.example.assignment.adil.calenderapp.presentation.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment.adil.calenderapp.data.api.TaskDetail
import com.example.assignment.adil.calenderapp.data.api.TaskModel
import com.example.assignment.adil.calenderapp.data.repository.CalendarRepositoryException
import com.example.assignment.adil.calenderapp.domain.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state.asStateFlow()
    val userId = 1

    init {
        _state.update { it.copy(selectedYearMonth = YearMonth.now()) }
        loadTasks()
    }

    fun onIntent(intent: CalendarIntent) {
        when (intent) {
            is CalendarIntent.LoadTasks -> loadTasks()
            is CalendarIntent.AddTask -> addTask(intent.userId, intent.task)
            is CalendarIntent.DeleteTask -> deleteTask(intent.userId, intent.taskId)
            is CalendarIntent.ChangeMonth -> changeMonth(intent.yearMonth)
            is CalendarIntent.SyncTasks -> syncTasks(intent.userId)
            is CalendarIntent.DismissError -> dismissError()
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                repository.getTaskList(userId).collect { tasks ->
                    _state.update { it.copy(tasks = tasks, isLoading = false) }
                }
            } catch (e: CalendarRepositoryException) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "An unexpected error occurred") }
            }
        }
    }

    private fun addTask(userId: Int, task: TaskModel) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                repository.addTask(userId, task)
                loadTasks() // Refresh the task list
            } catch (e: CalendarRepositoryException) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Failed to add task") }
            }
        }
    }

    private fun deleteTask(userId: Int, taskId: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                val result = repository.deleteTask(userId, taskId)
                if (result.isSuccess) {
                    loadTasks() // Refresh the task list
                } else {
                    _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
                }
            } catch (e: CalendarRepositoryException) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Failed to delete task") }
            }
        }
    }

    private fun changeMonth(yearMonth: YearMonth) {
        _state.update { it.copy(selectedYearMonth = yearMonth) }
    }

    private fun syncTasks(userId: Int) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null, isSyncing = true) }
                val result = repository.syncTasksToServer(userId)
                if (result.isSuccess) {
                    loadTasks() // Refresh the task list
                    _state.update { it.copy(isSyncing = false, lastSyncTime = System.currentTimeMillis()) }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            isSyncing = false,
                            error = result.exceptionOrNull()?.message
                        )
                    }
                }
            } catch (e: CalendarRepositoryException) {
                _state.update { it.copy(isLoading = false, isSyncing = false, error = e.message) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isSyncing = false, error = "Failed to sync tasks") }
            }
        }
    }

    private fun dismissError() {
        _state.update { it.copy(error = null) }
    }
} 