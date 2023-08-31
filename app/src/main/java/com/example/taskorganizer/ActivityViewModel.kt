package com.example.taskorganizer

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.taskorganizer.data.AppDataContainer
import com.example.taskorganizer.data.Category
import com.example.taskorganizer.data.Status
import com.example.taskorganizer.data.Task
import com.example.taskorganizer.repository.TaskRepository
import kotlinx.coroutines.flow.first

class ActivityViewModel: ViewModel() {


    lateinit var taskRepository:TaskRepository
    var selectedCategory = Category.All

     suspend fun updateTask( text:String, category:Int){
         selectedCategory = setCategory(category)
        var task = Task(0,text, /*Date().toString(),*/Status.Started,selectedCategory)
         taskRepository.insertTaskItem(task)
    }

    suspend fun getTasksList(context: Context,itemSelected:Int,updateTaskListener:UpdateTaskListener){
        taskRepository = AppDataContainer(context = context).taskRepository
        if(itemSelected == 0){
            var tasksList = taskRepository.getAllTasksStream().first()
            updateTaskListener.getTasksList(tasksList)
        }
        else{
            var tasksList = taskRepository.getSelectedCategoryTasks(setCategory(itemSelected)).first()
            updateTaskListener.getSelectedCategoryTasksList(tasksList)
        }

    }

    suspend fun getSelectedCategoryTaskslist(category: Int,updateTaskListener:UpdateTaskListener){

    }



    suspend fun deleteTask(task: Task,deleteListener:DeleteListener){
        taskRepository.deleteTaskItem(task = task)
        deleteListener.onTaskDeleted()

    }

    private fun setCategory(category: Int):Category{
       return  when(category){
            0 -> Category.All
            1 -> Category.Work
            2 -> Category.Personal
            3 -> Category.WishList
            4 -> Category.Birthday


           else -> {Category.All}
       }
    }


    interface UpdateTaskListener{
        fun getTasksList(taskList: List<Task>?)
        fun getSelectedCategoryTasksList(taskList: List<Task>?)

    }

    interface RemoveTaskListener{
        suspend fun removeTask(task: Task)
    }

    interface DeleteListener{
       suspend fun onTaskDeleted()
    }
}