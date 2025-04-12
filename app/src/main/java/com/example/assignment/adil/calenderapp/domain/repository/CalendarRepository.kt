package com.example.assignment.adil.calenderapp.domain.repository

import com.example.assignment.adil.calenderapp.data.api.TaskDetail
import com.example.assignment.adil.calenderapp.data.api.TaskModel
import kotlinx.coroutines.flow.Flow


interface CalendarRepository {
    suspend fun getTaskList(userId: Int): Flow<List<TaskDetail>>
    suspend fun addTask(userId: Int, task: TaskModel): Int
    suspend fun deleteTask(userId: Int, taskId: Int): Result<Boolean>
    suspend fun syncTasksToServer(userId: Int): Result<Boolean>
    suspend fun syncTasksFromServer(userId: Int): Result<Boolean>
    suspend fun deleteTasksFromLocal(userId: Int): Result<Boolean>
}