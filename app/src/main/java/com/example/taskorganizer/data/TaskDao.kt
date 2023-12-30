package com.example.taskorganizer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

 @Insert(onConflict = OnConflictStrategy.NONE)
 suspend fun insert(task: Task)

 @Update
 suspend fun update(task: Task)

 @Delete
 suspend fun delete(task:Task)

 @Query("SELECT * from tasks WHERE id = :id")
 fun getTaskItem(id:Int):Flow<Task>

 @Query("SELECT * from tasks ORDER BY status DESC")
 fun getAllTasks():Flow<List<Task>>

 @Query("SELECT * from tasks WHERE category == :selectedCategory ")
 fun getSelectedCategoryTasks(selectedCategory: Category):Flow<List<Task>>

 @Query("SELECT * from tasks WHERE taskDate == :selectedDate")
 fun getTasksOnSelectedDate(selectedDate:String):Flow<List<Task>>




}