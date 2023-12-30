package com.example.taskorganizer

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismiss

import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.taskorganizer.data.Status
import com.example.taskorganizer.data.Task
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


open class AllFragment: Fragment() {



    @SuppressLint("CoroutineCreationDuringComposition")
    lateinit  var removeTaskListener:ActivityViewModel.RemoveTaskListener

    fun setOnClickListener(listener:ActivityViewModel.RemoveTaskListener){
        this.removeTaskListener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         //super.onCreateView(inflater, container, savedInstanceState)


         return ComposeView(requireContext()).apply {
             setContent {
                getListFromBundle(arguments?.getParcelableArrayList<Task>("tasksList"))

             }
         }

    }

    @Composable
    fun getListFromBundle(tasksList:ArrayList<Task>?){
        tasksList?.let {
            if (it.isNotEmpty()) {
                LazyColumn(userScrollEnabled = true) {
                    items(it) {task->
                        loadCardView(task = task)
                    }
                }
            } else {
                Text(text = "There are no tasks for you,Tap + to add", fontSize = 20.sp)
            }
        }
    }



    @SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
      fun loadCardView(task: Task) {
        var isChecked by remember {
            mutableStateOf(false)
        }
        var isTaskRemoved by remember {
            mutableStateOf(false)
        }
        var isFromDatabase = false
        var currentFraction by remember{
            mutableStateOf(0f)
        }
        var dismissThreshold = 0.25f
       
        val dismissState = rememberDismissState(initialValue = DismissValue.Default,
                                       confirmValueChange = {
                                           if(it == DismissValue.DismissedToStart ){
                                               if(currentFraction>dismissThreshold){

                                               }
                                               //isTaskRemoved = true

                                           }


                                           true
                                       }, positionalThreshold = {

                1500.dp.toPx()

            }
                                           //dismissThreshold
            )

        var coroutineScope = rememberCoroutineScope()
        if (isTaskRemoved) {
            coroutineScope.launch {
                removeTaskListener.removeTask(task = task)
            }

        }
        var isDismissedToEnd = true
        SwipeToDismiss(state = dismissState, background = {
            setBackGround(dismissState)
            currentFraction  = dismissState.progress
            val coroutineScope = rememberCoroutineScope()
            /*if(dismissState.dismissDirection == DismissDirection.EndToStart && currentFraction<dismissThreshold){


                     coroutineScope.launch {
                         dismissState.reset()
                         //isDismissedToEnd = false
                     }

            }
              if(dismissState.dismissDirection == DismissDirection.StartToEnd){
                coroutineScope.launch {
                    dismissState.snapTo(DismissValue.Default)
                }

            }*/
        }, directions = setOf(DismissDirection.EndToStart), dismissContent = {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp)
        ) {
            Row(modifier = Modifier.padding(all = 10.dp)) {

                CustomCheckBox(isChecked = isChecked, onCheckedChange = {
                    isChecked = !isChecked
                    isFromDatabase = false
                })
                if(isChecked && !isFromDatabase){
                    coroutineScope.launch { removeTaskListener.updateTaskAsFinished(task = task) }

                }

                //Image(painter = painterResource(id = R.drawable.), contentDescription = )
                Column(modifier = Modifier.padding(start = 15.dp)) {

                    if(task.status == Status.Started)
                    Text(text = task.taskName, color = Color.Black, fontSize = 15.sp)
                    else{
                        Text(text = task.taskName, color = Color.Black, fontSize = 15.sp,
                            style = TextStyle(textDecoration = TextDecoration.LineThrough))
                        isChecked = true
                        isFromDatabase = true

                    }
                    Text(text = task.taskDate, color = Color.Black, fontSize = 10.sp)
                }
            }

            }
        })
    }

    fun formatDate(tasksDate:Date):String{
        var formatter = SimpleDateFormat("dd-MMM-yyyy")
        return formatter.format(tasksDate)
    }

    @Composable
    fun CustomCheckBox(isChecked:Boolean,onCheckedChange : (Boolean) -> Unit ){
        Icon(
             if (isChecked) painterResource(id = R.drawable.baseline_check_circle_24) else
                painterResource(id = R.drawable.outline_circle_24),
            "Checked Circle",Modifier.clickable {
                onCheckedChange(!isChecked)
            }
        )

    }




    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun setBackGround( dismissState: DismissState){
        var color = when(dismissState.dismissDirection){
            //DismissDirection.StartToEnd -> Color.Red
            DismissDirection.EndToStart ->Color.LightGray
            else -> Color.Transparent
        }

        val direction = dismissState.dismissDirection

        Row(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 5.dp)
            .background(color = color)) {
            /*if(direction == DismissDirection.StartToEnd){
                Icon( Icons.Filled.Delete, "delete Button" )
            }*/
             if(direction == DismissDirection.EndToStart){
                Icon(Icons.Rounded.Delete,"Reschedule Button")
            }
        }

    }

}