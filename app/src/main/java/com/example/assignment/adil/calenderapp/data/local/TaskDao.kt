package com.example.assignment.adil.calenderapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId AND (isSynced =0 OR (isSynced = 1 AND task_id IS NOT NULL) OR ((isSynced = 1 AND markForDeletion = 1))) ORDER BY date ASC")
    fun getAllTasksForUser(userId: Int): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Query("SELECT * FROM tasks WHERE userId = :userId AND isSynced = 0")
    fun getUnsyncedTasksForUser(userId: Int): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET isSynced = :isSynced, markForDeletion = :markForDeletion WHERE id = :taskId")
    suspend fun updateSyncStatus(taskId: Int, isSynced: Boolean, markForDeletion: Boolean)

    @Query("DELETE FROM tasks WHERE (id = :taskId OR task_id = :taskId)")
    suspend fun deleteTaskById(taskId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Query("SELECT * FROM tasks WHERE markForDeletion = 1")
    fun getAllTasksForDeletion(): Flow<List<TaskEntity>>

    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?


    

    
    @Query("UPDATE tasks SET isSynced = 1, task_id = :remoteId WHERE id = :localId")
    suspend fun markTaskAsSynced(localId: Int, remoteId: Int)

    @Query("SELECT * FROM tasks WHERE task_id = :remoteId")
    suspend fun getTaskByRemoteId(remoteId: Int): TaskEntity?



} 