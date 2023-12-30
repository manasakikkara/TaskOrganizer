package com.example.taskorganizer

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.taskorganizer.signIn.SignInResult
import com.example.taskorganizer.signIn.SignInState
import com.example.taskorganizer.data.AppDataContainer
import com.example.taskorganizer.data.Category
import com.example.taskorganizer.data.Status
import com.example.taskorganizer.data.Task
import com.example.taskorganizer.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ActivityViewModel: ViewModel() {


    lateinit var taskRepository:TaskRepository
    var selectedCategory = Category.All
    private val _list = mutableStateListOf<Task>()
    val list:List<Task> = _list
    var formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    var date  = formatter.format(Date())
    private val _state = MutableStateFlow(SignInState())
    //creating another value which is publicly available since
   // we dont want to expose mutablestateObjects to UI
    val state = _state.asStateFlow()

    fun onSignInResult(signInResult: SignInResult){
        _state.update { it.copy(isSuccessFull = signInResult.data!=null,
            signInError = signInResult.errormsg) }

    }

    fun resetState(){
        _state.update{
            SignInState()
        }
    }





     suspend fun insertTask(text:String, category:Int){
         selectedCategory = setCategory(category)
        var task = Task(0,text, date,Status.Started,selectedCategory)
         taskRepository.insertTaskItem(task)
    }


    fun formatDate():String{
       var formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        return formatter.format(date)
    }

    suspend fun getTasksList(context: Context,itemSelected:Int,updateTaskListener: UpdateTaskListener){
        var tasksList:List<Task>
        taskRepository = AppDataContainer(context = context).taskRepository
        if(itemSelected == 0){
            tasksList = taskRepository.getAllTasksStream().first()
           // _list.removeAll(tasksList)
            _list.clear()
            tasksList?.forEach {
                Log.i("log","tasksList adding and task selected is zero")

                _list.add(it)
            }
            updateTaskListener.getTasksList()
        }
        else{
            tasksList = taskRepository.getSelectedCategoryTasks(setCategory(itemSelected)).first()
            //_list.removeAll(taskRepository.getAllTasksStream().first())
            _list.clear()
            tasksList?.forEach {

                Log.i("log","tasksList adding and task selected not zero")
                _list.add(it)
            }
            updateTaskListener.getTasksList()
        }

    }



    fun navigateToMyProfileScreen(context: Context){
        //context.startActivity(Intent(context,MyProfile::class.java))
    }

    suspend fun deleteTask(task: Task,deleteListener:DeleteListener){
        taskRepository.deleteTaskItem(task = task)
        deleteListener.onTaskDeleted()

    }

    suspend fun updateTask(task: Task,updateListener: UpdateListener){
        task.status = Status.Completed
        taskRepository.updateTaskItem(task = task)
        updateListener.onTaskUpdated()

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

    fun convertMillisToDate(millis:Long?){

        millis?.let{
            var calendar = Calendar.getInstance()
            calendar.timeInMillis = millis + 86400000
            date = formatter.format(calendar.time)
        }
    }

    suspend fun getTasksOfTheDateSelected(tasksListListener: TasksListListener){
        var tasksList:List<Task>
        tasksList = taskRepository.getTasksOnSelectedDate(date).first()
        _list.clear()
        tasksList?.forEach {
            _list.add(it)
        }
        tasksListListener.onTasksDone()

    }


    interface TasksListListener{
        fun onTasksDone()
    }



    interface UpdateTaskListener{
        fun getTasksList()


    }

    interface RemoveTaskListener{
        suspend fun removeTask(task: Task)
        suspend fun updateTaskAsFinished(task: Task)
    }

    interface DeleteListener{
       suspend fun onTaskDeleted()

    }

    interface UpdateListener{
        suspend fun onTaskUpdated()
    }
}




