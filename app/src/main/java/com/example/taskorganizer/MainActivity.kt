package com.example.taskorganizer


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.BottomAppBar

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.taskorganizer.data.Task
import com.example.taskorganizer.databinding.AllFragmentBinding
import com.example.taskorganizer.signIn.GoogleAuthUIClient
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<ActivityViewModel>()
 var isFABClicked by mutableStateOf(false)
    var isLoadingDone by mutableStateOf(false)
    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(applicationContext,Identity.getSignInClient(applicationContext))
    }


 var note by mutableStateOf("All")
    var itemSelected by
        mutableStateOf(0)

    var fullText by
        mutableStateOf("")
    var isFABModifierClicked by mutableStateOf(false)
    var isTasksClicked by   mutableStateOf(true)
    var isCalendarClicked by mutableStateOf(false)
    var isProfileClicked by   mutableStateOf(false)




    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //var viewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application = application)
        //viewModel = viewModelFactory.create(ActivityViewModel::class.java)

        setContent {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                floatingActionButton =
                {
                    if(isTasksClicked || isCalendarClicked){
                        FloatingActionButton(onClick = {
                            isFABClicked = true
                        },modifier=Modifier.clickable {
                            //focusRequester.requestFocus()
                            isFABModifierClicked = true
                        }, shape = CircleShape) {
                            Icon(Icons.Filled.Add, "Add Task")
                        }
                    } else null
                },

                bottomBar = {
                    BottomAppBar(modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 0.dp, height = 50.dp),

                        containerColor = Color.LightGray,
                        content = {
                            Row(horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),

                            ) {
                                    IconButton(
                                        onClick = {
                                            isTasksClicked = true
                                            isCalendarClicked = false
                                            isProfileClicked = false
                                            isLoadingDone = false
                                                  },
                                        //modifier = Modifier.size(100.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_home_black_24dp),
                                            contentDescription = "this is image", modifier = Modifier.size(50.dp),
                                            tint = if (!isTasksClicked) Color.Black else Color.Blue
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            isCalendarClicked = true
                                            isProfileClicked = false
                                            isTasksClicked = false
                                                  },
                                        //modifier = Modifier.size(100.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                                            contentDescription = "this is image",modifier = Modifier.size(50.dp),
                                            tint = if (!isCalendarClicked) Color.Black else Color.Blue
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            isProfileClicked = true
                                            isTasksClicked = false
                                            isCalendarClicked = false
                                                  },
                                        //modifier = Modifier.size(100.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_person_24),
                                            contentDescription = "this is image",modifier = Modifier.size(50.dp),
                                            tint = if (!isProfileClicked) Color.Black else Color.Blue
                                        )
                                    }

                            }
                        })

                }, content = {
                    if(isCalendarClicked){
                        navigateToCalendarView()
                    }
                    if(isProfileClicked){
                        navigateToMyProfile()
                    }
                    Column {
                    if(isTasksClicked) {
                        setHorizontalList()

                        val coroutineScope = rememberCoroutineScope()
                        if (!isLoadingDone) {
                            val job = coroutineScope.launch {
                                delay(100)
                                getTasksFromViewModel(itemSelected)
                            }
                        }
                        LoadFragmentView(list = ArrayList(viewModel.list))
                    }

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
    })
        }
    }

    @Composable
    fun sendTask(){
        Log.i("log","sendTask started")

        if(fullText.isNotEmpty()) {
            sendTaskToViewModel(fullText)
            fullText = ""
            isLoadingDone = false
        }
        Log.i("log","sendTask ended")
    }

    suspend fun getTasksFromViewModel(itemSelected:Int){
        viewModel.getTasksList(
            applicationContext, itemSelected = itemSelected,object:ActivityViewModel.UpdateTaskListener{
                override fun getTasksList() {
                    isLoadingDone = true
                }

            })
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun sendTaskToViewModel(text:String){
         val coroutineScope = rememberCoroutineScope()
         coroutineScope.launch {
             viewModel.insertTask(text, itemSelected)
            // getTasksFromViewModel(itemSelected)
         }
    }

    @SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun showTextField(focusRequester: FocusRequester){
        var isIconButtonClicked by remember {
            mutableStateOf(false)
        }
        /*if(isIconButtonClicked){
            sendTask()
            isFABClicked = false
            //isLoading = true

        }*/
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
                        isIconButtonClicked = true

                        fullText = text

                    }) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Done button")
                    }
                })
            }
        if(isIconButtonClicked){
            sendTask()
            isFABClicked = false
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
               // delay(200)
                getTasksFromViewModel(itemSelected)
                //isLoadingDone = true
                isButtonClicked = false
            }

        }


     }
    @Composable
    fun navigateToCalendarView(){
        AndroidViewBinding(AllFragmentBinding::inflate) {
            supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                .replace(R.id.fragment_all, CalendarFragment()).commit()
        }
        // viewModel.navigateToCalendarActivity(LocalContext.current)

    }

    @Composable
    fun navigateToMyProfile(){
        AndroidViewBinding(AllFragmentBinding::inflate) {
            supportFragmentManager.beginTransaction().setReorderingAllowed(true)
                .replace(R.id.fragment_all, MyProfileFragment()).commit()
        }
    }



    @Composable
    fun LoadFragmentView(list:ArrayList<Task>?){
        Log.i("log","load fragment started")
        var removeTaskListener = object:ActivityViewModel.RemoveTaskListener{
            override suspend fun removeTask(task: Task) {
                viewModel.deleteTask(task = task,object:ActivityViewModel.DeleteListener{
                    override suspend fun onTaskDeleted() {
                        getTasksFromViewModel(itemSelected)
                    }

                })
            }

            override suspend fun updateTaskAsFinished(task: Task) {
                viewModel.updateTask(task = task,object:ActivityViewModel.UpdateListener{
                    override suspend fun onTaskUpdated() {
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


