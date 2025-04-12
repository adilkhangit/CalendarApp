package com.example.assignment.adil.calenderapp.data.repository

import com.example.assignment.adil.calenderapp.data.api.CalendarApiService
import com.example.assignment.adil.calenderapp.data.api.DeleteTaskRequest
import com.example.assignment.adil.calenderapp.data.api.TaskDetail
import com.example.assignment.adil.calenderapp.data.api.TaskListRequest
import com.example.assignment.adil.calenderapp.data.api.TaskModel
import com.example.assignment.adil.calenderapp.data.api.TaskRequest
import com.example.assignment.adil.calenderapp.data.local.TaskDao
import com.example.assignment.adil.calenderapp.data.local.TaskEntity
import com.example.assignment.adil.calenderapp.domain.repository.CalendarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import kotlin.math.absoluteValue

class CalendarRepositoryImpl @Inject constructor(
    private val apiService: CalendarApiService,
    private val taskDao: TaskDao,
    private val coroutineScope: CoroutineScope
) : CalendarRepository {

    override suspend fun getTaskList(userId: Int): Flow<List<TaskDetail>> = flow {
        try {
            taskDao.getAllTasksForUser(userId)
                .map { tasks ->
                    tasks.map { task ->
                        TaskDetail(
                            task_id = task.task_id ?: task.id,
                            task = task.toTaskModel()
                        )
                    }
                }
                .collect { emit(it) }
        } catch (e: Exception) {
            throw CalendarRepositoryException("Failed to get task list: ${e.message}", e)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addTask(userId: Int, task: TaskModel): Int = withContext(Dispatchers.IO) {
        try {
            val taskEntity = TaskEntity.fromTaskModel(userId, task, UUID.randomUUID().hashCode().absoluteValue)
            val result = taskDao.insertTask(taskEntity).toInt()
            
            // Launch sync in background
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    syncTasksToServer(userId)
                } catch (e: Exception) {
                    // Log sync failure but don't propagate error
                    println("Background sync failed: ${e.message}")
                }
            }
            
            result
        } catch (e: Exception) {
            throw CalendarRepositoryException("Failed to add task: ${e.message}", e)
        }
    }

    override suspend fun deleteTask(userId: Int, taskId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteCalendarTask(DeleteTaskRequest(userId, taskId))
            if (response.status == "Success") {
                taskDao.deleteTaskById(taskId)
                Result.success(true)
            } else {
                Result.failure(CalendarRepositoryException("Failed to delete task: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(CalendarRepositoryException("Failed to delete task: ${e.message}", e))
        }
    }

    override suspend fun syncTasksToServer(userId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val items = taskDao.getUnsyncedTasksForUser(userId).first()
            
            items.forEach { item ->
                try {
                    val response = apiService.storeCalendarTask(
                        TaskRequest(userId, TaskModel(item.title, item.description, item.date))
                    )
                    if (response.status == "Success") {
                        taskDao.updateSyncStatus(item.id, true, true)
                    }
                } catch (e: Exception) {
                    println("Failed to sync task ${item.id}: ${e.message}")
                    // Continue with other tasks even if one fails
                }
            }
            
            // Sync from server after pushing local changes
            syncTasksFromServer(userId)
            deleteTasksFromLocal(userId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(CalendarRepositoryException("Failed to sync tasks to server: ${e.message}", e))
        }
    }

    override suspend fun syncTasksFromServer(userId: Int): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCalendarTaskList(TaskListRequest(userId))
            val remoteTasks = response.tasks ?: emptyList()
            
            val transformedList = remoteTasks.map {
                TaskEntity.fromTaskModel(
                    userId = userId,
                    task = it.task_detail,
                    remoteId = it.task_id,
                ).copy(isSynced = true)
            }
            
            taskDao.insertTasks(transformedList)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(CalendarRepositoryException("Failed to sync tasks from server: ${e.message}", e))
        }
    }

    override suspend fun deleteTasksFromLocal(userId: Int): Result<Boolean> {
        val list = taskDao.getAllTasksForDeletion().first()
        list.forEach {
            it.task_id?.let { id->
                taskDao.deleteTaskById(id)
            }
        }
        return Result.success(true)
    }
}

class CalendarRepositoryException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)