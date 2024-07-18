package com.example.taskorganizer

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    private val _signInResult = MutableStateFlow(SignInResult(null,null))
    val result = _signInResult.asStateFlow()
    private val _totalCount = MutableStateFlow(0)
    val totalCount = _totalCount.asStateFlow()
    private val _totalFinished = MutableStateFlow(0)
    val totalFinished = _totalFinished.asStateFlow()
    //private val _totalCategoryTasks = MutableStateFlow(0)
    //val totalCategoryTasks = _totalCategoryTasks.asStateFlow()
    var totalWorkTasks by mutableStateOf(0)
     var totalBirthdayTasks by mutableStateOf(0)
    var totalPersonalTasks by mutableStateOf(0)
    var totalWishlistTasks by mutableStateOf(0)

    fun onSignInResult(signInResult: SignInResult){
        _state.update { it.copy(isSuccessFull = signInResult.data!=null,
            signInError = signInResult.errormsg) }
        if(signInResult.data!= null)
            _signInResult.update { it.copy(signInResult.data,signInResult.errormsg) }

    }

    fun resetState(){
        _state.update{
            SignInState()
        }
    }

    suspend fun getTotalTasks(){
        _totalCount.update { taskRepository.getTotalTasks() }
    }

    suspend fun getTotalFinishedTasks(){
        _totalFinished.update {  taskRepository.getTotalFinishedTasks(Status.Completed)}
    }

     suspend fun getTasksCountOnSelectedCategory(){
         //_totalCategoryTasks.update { taskRepository.getTasksCountOnSelectedCategory(selectedCategory) }
         totalWorkTasks = taskRepository.getTasksCountOnSelectedCategory(Category.Work)
         totalBirthdayTasks = taskRepository.getTasksCountOnSelectedCategory(Category.Birthday)
         totalPersonalTasks = taskRepository.getTasksCountOnSelectedCategory(Category.Personal)
         totalWishlistTasks = taskRepository.getTasksCountOnSelectedCategory(Category.WishList)
     }







     suspend fun insertTask(text: String, category: Int, selectedSheetCategory: Category){

         selectedCategory = setCategory(category)
         if(selectedSheetCategory != Category.All) {
              selectedCategory = selectedSheetCategory
         }
         var task = Task(0, text, date, Status.Started, selectedCategory)

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

    suspend fun updateTask(task: Task,updateListener: UpdateListener){
        task.status = Status.Completed
        taskRepository.updateTaskItem(task = task)
        updateListener.onTaskUpdated()
    }

    fun convertMillisToDate(millis:Long?):String{

        millis?.let{
            var calendar = Calendar.getInstance()
            calendar.timeInMillis = millis + 86400000
            date = formatter.format(calendar.time)
        }
        return date
    }

    fun convertMillisToDay(millis: Long?):Int{
        var day  = 0
        millis?.let{
            var calendar = Calendar.getInstance()
            calendar.timeInMillis = millis + 86400000
            date = formatter.format(calendar.time)
             day = calendar.get(Calendar.DAY_OF_MONTH)
        }
        return day
    }

    suspend fun getTasksOfTheDateSelected(tasksListListener: TasksListListener,date: String){
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

    interface TaskStatusListener {

         fun getTotalTasks(totalTasks:Int)
         fun getTotalFinishedTasks(totalFinishedTasks:Int)
    }
}




