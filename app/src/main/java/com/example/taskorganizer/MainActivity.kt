package com.example.taskorganizer


import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.example.taskorganizer.data.Category
import com.example.taskorganizer.data.Task
import com.example.taskorganizer.databinding.AllFragmentBinding
import com.example.taskorganizer.notifications.TaskNotificationService
import com.example.taskorganizer.notifications.TodoApplication
import com.example.taskorganizer.signIn.GoogleAuthUIClient
import com.example.taskorganizer.ui.theme.Purple40
import com.example.taskorganizer.ui.theme.PurpleGrey80
import com.example.taskorganizer.ui.theme.TaskOrganizerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.Permission
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<ActivityViewModel>()
 var isFABClicked by mutableStateOf(false)
    var isLoadingDone by mutableStateOf(false)

    var selectedText by
        mutableStateOf(Category.All)
    var day by
        mutableIntStateOf(0)


 var note by mutableStateOf("All")
    var itemSelected by
        mutableStateOf(0)

    var fullText by
        mutableStateOf("")
    var isTasksClicked by   mutableStateOf(true)
    var isCalendarClicked by mutableStateOf(false)
    var isProfileClicked by   mutableStateOf(false)
    var showBottomSheet by mutableStateOf(true)
    var isCalendarIconSheetClicked by
             mutableStateOf(false)





    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
        ExperimentalLayoutApi::class, ExperimentalPermissionsApi::class
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            TaskOrganizerTheme {
                val taskNotificationPermission =
                    rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
                LaunchedEffect(key1 = true, block = {
                    if(!taskNotificationPermission.status.isGranted){
                        taskNotificationPermission.launchPermissionRequest()
                    } 
                })
                val taskNotificationService = TaskNotificationService(this)
                taskNotificationService.showBasicNotification()
                intent.extras?.let{
                    if(it.getBoolean("FromNotifications")){
                        isFABClicked = true
                    }
                }
                
            }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                floatingActionButton =
                {
                    if(isTasksClicked || isCalendarClicked){
                        FloatingActionButton(onClick = {
                            isFABClicked = true
                        }, shape = CircleShape) {
                            Icon(Icons.Filled.Add, "Add Task")
                        }
                    } else null
                },

                bottomBar = {
                    BottomAppBar(modifier = Modifier
                        .fillMaxWidth()
                        .size(width = 0.dp, height = 50.dp),

                        containerColor = PurpleGrey80,
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
                                            tint = if (!isTasksClicked) Color.Black else Purple40
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
                                            tint = if (!isCalendarClicked) Color.Black else Purple40
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
                                            tint = if (!isProfileClicked) Color.Black else Purple40
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
                   var  sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    val focusRequester  = remember {
                        FocusRequester()
                    }
                    LaunchedEffect(key1 = isFABClicked, block = {
                        delay(200)
                        focusRequester.requestFocus()
                    })
                    ModalBottomSheet(onDismissRequest = { isFABClicked = false},
                        modifier = Modifier
                            .focusRequester(focusRequester)

                            .wrapContentHeight()
                            .fillMaxHeight(0.60f),
                             sheetState = sheetState
                            ) {
                        Column(modifier = Modifier.wrapContentHeight()) {


                            showTextField(focusRequester)
                            Row(
                                modifier = Modifier.padding(
                                    top = 10.dp,
                                    bottom = 10.dp,
                                    start = 4.dp
                                ).wrapContentHeight()
                            ) {
                                loadCategoryView(focusRequester)
                                loadCalendarView(focusRequester)

                            }
                        }


                    }


                }
    })
        }
    }








    @Composable
    fun loadCategoryView(focusRequester: FocusRequester) {
        var list = listOf<Category>(Category.All,Category.Work,
            Category.Personal,Category.WishList,Category.Birthday)
        var isExpanded by remember{
            mutableStateOf(false)
        }
        var isClicked by remember{
            mutableStateOf(false)
        }
        if(!isClicked) {
            when (itemSelected) {
                0 -> selectedText = Category.All
                1 -> selectedText = Category.Work
                2 -> selectedText = Category.Personal
                3 -> selectedText = Category.WishList
                4 -> selectedText = Category.Birthday
            }
        }


        Box (modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color = Purple40)
            ){
            Text(text = "Category: $selectedText", color = Color.White, fontSize = 18.sp, modifier = Modifier
                .padding(10.dp)
                .clickable {
                    isExpanded = !isExpanded
                    focusRequester.requestFocus()
                })
        }
        DropdownMenu(expanded = isExpanded , onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .offset(x = 7.dp)) {
            list.forEach {
                DropdownMenuItem(text = { Text(text = it.toString()) }, onClick = {
                    isClicked = true
                    selectedText = it
                    isExpanded = false
                    focusRequester.requestFocus()
                })


            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun loadCalendarView(focusRequester: FocusRequester) {

        val focusManager = LocalFocusManager.current

        IconButton(onClick = {
            isCalendarIconSheetClicked = true
        }) {
            if(day == 0) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                    contentDescription = "this is image", modifier = Modifier.size(80.dp),
                    tint = if (!isCalendarIconSheetClicked) Purple40 else Color.Black
                )
            }
            else{
                Box() {
                    Icon(painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                        contentDescription = "selectedDay", modifier = Modifier
                            .size(50.dp)
                            .padding(1.dp), tint = Purple40)
                    Text(text = day.toString(), modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 4.dp),
                        fontSize = 18.sp, color = Purple40
                    )
                }

            }
        }
        if(isCalendarIconSheetClicked){
            focusManager.clearFocus()
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { 
                Button(onClick = {
                    day = viewModel.convertMillisToDay(datePickerState.selectedDateMillis)
                    isCalendarIconSheetClicked = false
                    focusRequester.requestFocus()
                }) {
                   Text(text = "Done") 
                }
            }, dismissButton = {
                Button(onClick = {
                    isCalendarIconSheetClicked = false
                    focusRequester.requestFocus()
                }) {
                   Text(text = "Cancel")
                }
            }) {
                DatePicker(state = datePickerState)
            }
                

        }

    }

    @Composable
    fun sendTask(){
        Log.i("log","sendTask started")

        if(fullText.isNotEmpty()) {
            sendTaskToViewModel(fullText,selectedText,day)
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
    fun sendTaskToViewModel(text: String, selectedText: Category, day: Int){
         val coroutineScope = rememberCoroutineScope()
         coroutineScope.launch {
             viewModel.insertTask(text, itemSelected,selectedText)
            // getTasksFromViewModel(itemSelected)
         }
    }

    @SuppressLint("SuspiciousIndentation", "CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun showTextField(focusRequester: FocusRequester){
        var isSendButtonClicked by remember {
            mutableStateOf(false)
        }

        var text by remember {
            mutableStateOf("")
        }

        TextField(value = text, modifier = Modifier.fillMaxWidth(), onValueChange = {
                     text = it }, label = {}, trailingIcon = {
                     IconButton(onClick = {
                         isSendButtonClicked = true
                         fullText = text
                     }) {
                         Icon(
                             painterResource(R.drawable.baseline_send_24),
                             contentDescription = "Done button", tint = Purple40,
                             modifier = Modifier.size(30.dp)
                         ) } })
        if(isSendButtonClicked){
            sendTask()
            isFABClicked = false
            day = 0
            selectedText = Category.All
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

        Box (modifier = Modifier
            .clip(RoundedCornerShape(60.dp))
            .background(color = PurpleGrey80)){
            Text(text = "No Category", fontSize = 20.sp, modifier = Modifier.padding(10.dp))
        }
    }
    
}


