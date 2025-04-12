package com.example.assignment.adil.calenderapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.assignment.adil.calenderapp.data.api.TaskModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "tasks", primaryKeys = ["task_id"])
data class TaskEntity(
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val description: String,
    val date: String,
    val isSynced: Boolean = false,
    val task_id: Int? = null,
    val markForDeletion: Boolean = false
) {
    fun toTaskModel(): TaskModel {
        return TaskModel(
            title = title,
            description = description,
            date = date
        )
    }

    companion object {
        fun fromTaskModel(
            userId: Int,
            task: TaskModel,
            remoteId: Int? = null
        ): TaskEntity {
            return TaskEntity(
                userId = userId,
                title = task.title,
                description = task.description,
                date = task.date,
                task_id = remoteId
            )
        }
    }
} 