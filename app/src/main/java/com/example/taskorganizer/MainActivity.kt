package com.example.taskorganizer


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskorganizer.data.Task
import com.example.taskorganizer.databinding.AllFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

 lateinit var viewModel: ActivityViewModel
 var isFABClicked by mutableStateOf(false)
 var note by mutableStateOf("All")
    var itemSelected by
        mutableStateOf(0)
    var tasksList:List<Task>?  by mutableStateOf(arrayListOf())
    var fullText by
        mutableStateOf("")
    var isFABModifierClicked by mutableStateOf(false)



    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application = application)
        viewModel = viewModelFactory.create(ActivityViewModel::class.java)

        setContent {


            Scaffold(
                modifier = Modifier.fillMaxSize(),
                floatingActionButton = {
                    FloatingActionButton(onClick = {

                      isFABClicked = true

                    },modifier=Modifier.clickable {
                        //focusRequester.requestFocus()
                        isFABModifierClicked = true



                    }, shape = CircleShape) {
                        Icon(Icons.Filled.Add, "Add Task")
                    }
                },

                bottomBar = {
                    BottomAppBar(modifier = Modifier
                        .fillMaxWidth(),

                        containerColor = Color.Green,
                        content = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var isTasksClicked by remember { mutableStateOf(false) }
                                var isCalendarClicked by remember { mutableStateOf(false) }
                                var isProfileClicked by remember { mutableStateOf(false) }
                                var isMenuClicked by remember { mutableStateOf(false) }

                                Row {
                                    IconButton(
                                        onClick = { isMenuClicked = !isMenuClicked },
                                        modifier = Modifier.size(100.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                            contentDescription = "this is image",
                                            tint = if (!isTasksClicked) Color.Black else Color.Blue
                                        )
                                    }
                                    IconButton(
                                        onClick = { isTasksClicked = !isTasksClicked },
                                        modifier = Modifier.size(100.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                            contentDescription = "this is image",
                                            tint = if (!isTasksClicked) Color.Black else Color.Blue
                                        )
                                    }
                                    IconButton(
                                        onClick = { isCalendarClicked = !isCalendarClicked },
                                        modifier = Modifier.size(100.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                            contentDescription = "this is image",
                                            tint = if (!isCalendarClicked) Color.Black else Color.Blue
                                        )
                                    }
                                    IconButton(
                                        onClick = { isProfileClicked = !isProfileClicked },
                                        modifier = Modifier.size(100.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                            contentDescription = "this is image",
                                            tint = if (!isProfileClicked) Color.Black else Color.Blue
                                        )
                                    }
                                }
                            }
                        })

                }, content = {  Column {
                    setHorizontalList()
                    val coroutineScope = rememberCoroutineScope()
                    coroutineScope.launch {
                       getTasksFromViewModel(itemSelected)
                    }

                    LoadFragmentView(list = ArrayList(tasksList))
                }
                if(isFABClicked){
                    val focusRequester  = remember {
                        FocusRequester()
                    }
                    LaunchedEffect(key1 = isFABClicked, block = {
                        delay(200)
                        focusRequester.requestFocus()
                    })
                    showTextField(focusRequester)

                }
                else{
                    sendTask()

                }


    })
        }
    }
    @Composable
    fun sendTask(){
        if(!isFABClicked)
        if(fullText.isNotEmpty()) {
            sendTaskToViewModel(fullText)
            fullText = ""
        }
    }

    suspend fun getTasksFromViewModel(itemSelected:Int){
        viewModel.getTasksList(
            applicationContext, itemSelected = itemSelected,
            updateTaskListener = object : ActivityViewModel.UpdateTaskListener {
                override fun getTasksList(taskList: List<Task>?) {
                    tasksList = taskList
                }

                override fun getSelectedCategoryTasksList(taskList: List<Task>?) {
                    tasksList = taskList
                }
            })
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun sendTaskToViewModel(text:String){
         val coroutineScope = rememberCoroutineScope()
         coroutineScope.launch {
             viewModel.updateTask(text, itemSelected)
         }
    }

    @SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun showTextField(focusRequester: FocusRequester){
        var isIconButtonClicked by remember{
            mutableStateOf(false)
        }
        var text by remember {
            mutableStateOf("")
        }
            if(isFABClicked) {
               var textField =  TextField(value = text, modifier = Modifier
                   .focusRequester(focusRequester)
                   .fillMaxWidth(), onValueChange = {
                    text = it

                }, label = {}, trailingIcon = {
                    IconButton(onClick = {
                        isFABClicked = false
                        isIconButtonClicked = true
                        fullText = text

                    }) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Done button")
                    }
                })
            }
    }


    @Composable
     fun setHorizontalList() {
        var list = listOf("All","Work","Personal","Wishlist","Birthday")

       LazyRow(userScrollEnabled = true, modifier = Modifier.padding(10.dp)){
           itemsIndexed(list){ index,item ->
               setButtonView(str =item,index )

           }
       } // TO-DO
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
     fun setButtonView(str:String,index:Int){
        var isButtonClicked by remember {
            mutableStateOf(false)
        }
         Button(onClick = {  //isClicked = !isClicked
             isButtonClicked = true
             itemSelected = if(itemSelected != index) index else 0

         }, enabled = itemSelected != index,  modifier = Modifier

             .padding(5.dp)
              ){
                Text(text = str)
                note = str
         }
        if(isButtonClicked) {
            var coroutineScope = rememberCoroutineScope()
            coroutineScope.launch {
                getTasksFromViewModel(itemSelected)
            }
        }
     }



    @Composable
    fun LoadFragmentView(list:ArrayList<Task>?){
        var removeTaskListener = object:ActivityViewModel.RemoveTaskListener{
            override suspend fun removeTask(task: Task) {
                viewModel.deleteTask(task = task,object:ActivityViewModel.DeleteListener{
                    override suspend fun onTaskDeleted() {
                        getTasksFromViewModel(itemSelected)
                    }

                })
            }

        }
            var instance:Fragment = AllFragment()

            var bundle = Bundle()

            when (itemSelected) {
                0 ->
                {
                    instance = AllFragment()
                    instance.setOnClickListener(removeTaskListener)
                }
                1 -> {
                    instance = WorkFragment()
                    instance.setOnClickListener(removeTaskListener)
                }
                2 -> {
                    instance = PersonalFragment()
                    instance.setOnClickListener(removeTaskListener)
                }
                3 -> {
                    instance = WishListFragment()
                    instance.setOnClickListener(removeTaskListener)
                }
                4 -> {
                    instance = BirthdayFragment()
                    instance.setOnClickListener(removeTaskListener)
                }

            }

            bundle.putParcelableArrayList("tasksList",list)
            instance.arguments  = bundle

            AndroidViewBinding(AllFragmentBinding::inflate) {
                // val allFragment = fragmentAll.getFragment<AllFragment>()
                supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                    .replace(R.id.fragment_all, instance).commit()
            }
    }



    @Composable
 fun setBottomNavigation(){
     var isTasksClicked by remember { mutableStateOf(false) }
     var isCalendarClicked by remember { mutableStateOf(false) }
     var isProfileClicked by remember { mutableStateOf(false) }
     var isMenuClicked by remember { mutableStateOf(false) }
     //Box(modifier = Modifier.fillMaxSize()) {

         /*NavigationBar(modifier = Modifier
             .fillMaxHeight()
             .fillMaxWidth(0.4f), containerColor = Color.Blue) {
             Column(modifier = Modifier
                 .fillMaxHeight()
                 .fillMaxWidth(0.4f)){
                 Column(){

                     IconButton(
                         onClick = {  },
                         modifier = Modifier.size(100.dp),


                     ) {
                         Icon(
                             painter = painterResource(id = R.drawable.ic_launcher_foreground),
                             contentDescription = "this is image",

                             )
                     }
                     IconButton(
                         onClick = {  },
                         modifier = Modifier.size(100.dp)
                     ) {
                         Icon(
                             painter = painterResource(id = R.drawable.ic_launcher_foreground),
                             contentDescription = "this is image",

                             )
                     }
                     IconButton(
                         onClick = {  },
                         modifier = Modifier.size(100.dp)
                     ) {
                         Icon(
                             painter = painterResource(id = R.drawable.ic_launcher_foreground),
                             contentDescription = "this is image",

                             )
                     }
                     IconButton(
                         onClick = {  },
                         modifier = Modifier.size(100.dp)
                     ) {
                         Icon(
                             painter = painterResource(id = R.drawable.ic_launcher_foreground),
                             contentDescription = "this is image",

                             )
                     }
                 }
             }
         }*/


         BottomAppBar(modifier = Modifier
             .fillMaxWidth()
             /*.align(alignment = Alignment.BottomEnd)*/, containerColor = Color.Green, content = {
             Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.SpaceEvenly,
                 verticalAlignment = Alignment.CenterVertically
             ) {




                 Row {
                     IconButton(
                         onClick = { isMenuClicked = !isMenuClicked },
                         modifier = Modifier.size(100.dp)
                     ) {
                         Icon(
                             painter = painterResource(id = R.drawable.ic_launcher_foreground),
                             contentDescription = "this is image",
                             tint = if (!isTasksClicked) Color.Black else Color.Blue
                         )
                     }
                     IconButton(
                         onClick = { isTasksClicked = !isTasksClicked },
                         modifier = Modifier.size(100.dp)
                     ) {
                         Icon(
                             painter = painterResource(id = R.drawable.ic_launcher_foreground),
                             contentDescription = "this is image",
                             tint = if (!isTasksClicked) Color.Black else Color.Blue
                         )
                     }
                     IconButton(
                         onClick = { isCalendarClicked = !isCalendarClicked },
                         modifier = Modifier.size(100.dp)
                     ) {
                         Icon(
                             painter = painterResource(id = R.drawable.ic_launcher_foreground),
                             contentDescription = "this is image",
                             tint = if (!isCalendarClicked) Color.Black else Color.Blue
                         )
                     }
                     IconButton(
                         onClick = { isProfileClicked = !isProfileClicked },
                         modifier = Modifier.size(100.dp)
                     ) {
                         Icon(
                             painter = painterResource(id = R.drawable.ic_launcher_foreground),
                             contentDescription = "this is image",
                             tint = if (!isProfileClicked) Color.Black else Color.Blue
                         )
                     }
                 }
             }
         })


 }

    @Preview
    @Composable
    fun previewScreen(){

            setBottomNavigation()
    }
    
}


