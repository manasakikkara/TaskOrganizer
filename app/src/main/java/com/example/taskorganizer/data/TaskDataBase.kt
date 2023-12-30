package com.example.taskorganizer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class TaskDataBase: RoomDatabase() {

    abstract fun taskDao():TaskDao

    companion object{
        @Volatile
        var instance:TaskDataBase? = null
        fun getDataBaseInstance(context: Context):TaskDataBase{
            return instance?: synchronized(this){
                Room.databaseBuilder(context,TaskDataBase::class.java,"task_database")
                    .build().also { instance = it }
            }

        }
    }
}