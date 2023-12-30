package com.example.taskorganizer.repository

import com.example.taskorganizer.data.Category
import com.example.taskorganizer.data.Task
import com.example.taskorganizer.data.TaskDao
import kotlinx.coroutines.flow.Flow


class OfflineTaskRepository(var taskDao: TaskDao):TaskRepository {
    override suspend fun getAllTasksStream(): Flow<List<Task>> =
        taskDao.getAllTasks()


    override suspend fun getTaskItem(id:Int): Flow<Task> =
        taskDao.getTaskItem(id)



    override suspend fun insertTaskItem(task: Task)
       = taskDao.insert(task)


    override suspend fun deleteTaskItem(task: Task)
       = taskDao.delete(task)


    override suspend fun updateTaskItem(task: Task)
       = taskDao.update(task)

    override suspend fun getSelectedCategoryTasks(selectedCategory: Category): Flow<List<Task>>
        = taskDao.getSelectedCategoryTasks(selectedCategory)

    override suspend fun getTasksOnSelectedDate(selectedDate: String): Flow<List<Task>>
        = taskDao.getTasksOnSelectedDate(selectedDate)



}