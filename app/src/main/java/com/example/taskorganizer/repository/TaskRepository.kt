package com.example.taskorganizer.repository

import com.example.taskorganizer.data.Category
import com.example.taskorganizer.data.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

suspend fun getAllTasksStream():Flow<List<Task>>

suspend fun getTaskItem(id:Int):Flow<Task>

suspend fun insertTaskItem(task: Task)

suspend fun deleteTaskItem(task: Task)

suspend fun updateTaskItem(task: Task)

suspend fun getSelectedCategoryTasks(selectedCategory: Category):Flow<List<Task>>
}