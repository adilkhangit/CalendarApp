package com.example.assignment.adil.calenderapp.data.api

import retrofit2.http.Body
import retrofit2.http.POST

interface CalendarApiService {
    @POST("api/storeCalendarTask")
    suspend fun storeCalendarTask(
        @Body request: TaskRequest
    ): TaskResponse

    @POST("api/getCalendarTaskList")
    suspend fun getCalendarTaskList(
        @Body request: TaskListRequest
    ): TaskListResponse

    @POST("api/deleteCalendarTask")
    suspend fun deleteCalendarTask(
        @Body request: DeleteTaskRequest
    ): DeleteTaskResponse
}

data class TaskRequest(
    val user_id: Int, val task: TaskModel
)

data class TaskListRequest(
    val user_id: Int
)

data class DeleteTaskRequest(
    val user_id: Int, val task_id: Int
)

data class TaskResponse(
    val status: String
)

data class TaskListResponse(
    val tasks: List<RemoteTask>? = null
)

data class RemoteTask(
    val task_id: Int, val task_detail: TaskModel
)

data class DeleteTaskResponse(
    val status: String
)

data class TaskModel(
    val title: String, val description: String, val date: String
)

data class TaskDetail(
    val task_id: Int, val task: TaskModel
) 