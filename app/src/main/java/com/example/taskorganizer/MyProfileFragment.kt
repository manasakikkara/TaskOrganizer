package com.example.taskorganizer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.IntentSenderRequest.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.taskorganizer.signIn.GoogleAuthUIClient
import com.example.taskorganizer.signIn.SignInResult
import com.example.taskorganizer.signIn.SignInState
import com.example.taskorganizer.signIn.UserData
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.taskorganizer.data.Category
import com.example.taskorganizer.data.PieChartInput
import com.example.taskorganizer.ui.theme.Blue200
import com.example.taskorganizer.ui.theme.Pink40
import com.example.taskorganizer.ui.theme.Pink80
import com.example.taskorganizer.ui.theme.Purple40
import com.example.taskorganizer.ui.theme.Purple80
import com.example.taskorganizer.ui.theme.PurpleGrey40
import com.example.taskorganizer.ui.theme.PurpleGrey80
import com.example.taskorganizer.ui.theme.Yellow200
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class MyProfileFragment: AllFragment() {

    private val viewModel:ActivityViewModel by viewModels(::requireActivity)
    private val googleAuthUIClient by lazy {
        val context = activity?.applicationContext
        context?.let {
            GoogleAuthUIClient(it, Identity.getSignInClient(it))
        }

    }
     var userData:UserData? by mutableStateOf(null)
    var isSignedOut by mutableStateOf(false)







    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         //super.onCreateView(inflater, container, savedInstanceState)
         return ComposeView(requireContext()).apply {
             setContent {
                 Column() {
                     val state by viewModel.state.collectAsStateWithLifecycle()
                     val totalCount by viewModel.totalCount.collectAsStateWithLifecycle()
                    /* var totalCount by remember{
                         mutableStateOf(0)
                     }
                     var totalFinished by remember{
                         mutableStateOf(0)
                     }*/
                     Card(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(10.dp)
                     ) {
                        val coroutineScope = rememberCoroutineScope()
                         coroutineScope.launch(Dispatchers.IO) {
                            // delay(100)
                            /*viewModel.getTotalTasksAndFinishedTasks(object:ActivityViewModel.TaskStatusListener{
                                override fun getTotalTasks(totalTasks: Int) {
                                    totalCount = totalTasks
                                }

                                override fun getTotalFinishedTasks(totalFinishedTasks: Int) {
                                   totalFinished = totalFinishedTasks
                                }

                            })*/
                             viewModel.getTotalTasks()
                             viewModel.getTotalFinishedTasks()
                         }


                         val launcher = rememberLauncherForActivityResult(
                             contract = ActivityResultContracts.StartIntentSenderForResult(), onResult = { activityResult ->
                                 if(activityResult.resultCode == AppCompatActivity.RESULT_OK){
                                     lifecycleScope.launch {
                                         val signInResult =
                                             googleAuthUIClient?.signInWithIntent(intent = activityResult.data?: return@launch)
                                                 ?: SignInResult(null,"googleAuthUiClient is null")
                                         viewModel.onSignInResult(signInResult = signInResult)
                                     }
                                 }
                             } )

                         LaunchedEffect(key1 = state.isSuccessFull){
                             if(state.isSuccessFull){
                                 Toast.makeText(context,"signIn successful",Toast.LENGTH_LONG).show()
                             }
                         }

                        SignInScreen(state = state, onSignInClick = {
                            lifecycleScope.launch {
                                        googleAuthUIClient?.let{
                                            val signInIntentSender =  googleAuthUIClient?.signIn()
                                            launcher.launch(IntentSenderRequest.Builder(signInIntentSender?:return@launch).build())
                                        }
                                }
                        })
                     }
                     if (state.isSuccessFull || userData!=null){
                     signOutCard(onClick = {
                         lifecycleScope.launch {
                             googleAuthUIClient?.let{
                                 it.signOut()
                                 userData = it.getSignedInUser()
                                viewModel.resetState()

                             }

                         }
                         Toast.makeText(context,"SignOut SuccessFul",Toast.LENGTH_SHORT).show()

                     })
                     }
                     taskStatusCard(totalCount)
                     loadAndroidPieChart(totalCount)
                 }
             }
         }

    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun loadAndroidPieChart(totalSum:Int){

        //val totalCategoryTasks by viewModel.totalCategoryTasks.collectAsStateWithLifecycle()
        val totalWorkTasks  = viewModel.totalWorkTasks
        val totalBirthdayTasks  = viewModel.totalBirthdayTasks
        val totalPersonalTasks  = viewModel.totalPersonalTasks
        val totalWishListTasks  = viewModel.totalWishlistTasks

        var startAngle = 0f
        var sweepAngle  = 0f
        var list = mutableListOf<PieChartInput>()
        val coroutineScope = rememberCoroutineScope()
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.getTasksCountOnSelectedCategory()
        }

        list.add(PieChartInput(Purple40,totalWorkTasks,"Work"))
        list.add(PieChartInput(Pink80,totalBirthdayTasks,"Birthday"))
        list.add(PieChartInput(Pink40,totalPersonalTasks,"Personal"))
        list.add(PieChartInput(Yellow200,totalWishListTasks,"Wishlist"))
        list.add(PieChartInput(Blue200,totalSum,"totalTasks"))

        var fullCount = 0

        for (i  in list) {
            fullCount += i.value
        }

        var animationPlayed by remember{
            mutableStateOf(false)
        }
        val animatedSize by animateFloatAsState(targetValue = if(animationPlayed) 125f else 0f,
            label = "animating Pie chart", animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing)
        )

        val animateRadius by animateFloatAsState(targetValue = if (animationPlayed) 180f  else 0f,
            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing), label = "animating by radius"
        )

        LaunchedEffect(key1 = true){
            animationPlayed = true
        }

        Card(modifier = Modifier.fillMaxWidth().padding(10.dp)) {



        Row() {



            Box(modifier = Modifier.size(animatedSize.dp).padding(15.dp)) {


                Canvas(
                    modifier = Modifier
                        .size(160.dp)
                        .rotate(animateRadius)
                ) {
                    val width = size.width
                    val radius = width / 2f
                    val strokeWidth = radius * .3f
                    list.forEach {
                        sweepAngle = ((360 * it.value).toFloat()) / (fullCount.toFloat())
                        drawArc(
                            color = it.color,
                            startAngle,
                            sweepAngle,
                            false,
                            style = Stroke(strokeWidth),
                            size = Size(width - strokeWidth, width - strokeWidth)
                        )
                        startAngle += sweepAngle
                    }


                }
            }

         loadPieChartDetailsCard(pieChartList = list)
        }
        }


    }

    @Composable
    fun detailsItemView(paintColor: Color,desc:String){
        Row {
            Canvas(modifier = Modifier
                .size(20.dp)
                .padding(3.dp) ){
                drawCircle(color = paintColor)
            }
            Text(text = desc, fontSize = 15.sp)
        }
    }

    @Composable
    fun loadPieChartDetailsCard(pieChartList:MutableList<PieChartInput>){
        Column(modifier = Modifier.padding(20.dp)) {
            pieChartList.forEach {
                detailsItemView(paintColor = it.color, desc = (it.description + " - " + it.value))
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun signOutCard(onClick:() -> Unit){
        Card (modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), onClick = {
            //viewModel.resetState()
            onClick.invoke()

        }){
            Text("Sign Out", modifier = Modifier.padding(15.dp))
        }
    }



    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
     fun taskStatusCard(totalCount: Int){


            val totalFinished by viewModel.totalFinished.collectAsStateWithLifecycle()



           Row(modifier = Modifier
               .fillMaxWidth()
               .padding(5.dp)
               ){
              Card(modifier = Modifier
                  .weight(1f)
                  .padding(3.dp)){
                  Column {
                      Text("Total Tasks", modifier = Modifier.padding(15.dp))
                      Text(totalCount.toString(), fontSize = 20.sp, modifier = Modifier.padding(horizontal = 55.dp, vertical = 20.dp))
                  }
              }
               Card(
                   Modifier
                       .weight(1f)
                       .padding(3.dp)){
                   Column {
                       Text("Finished Tasks", modifier = Modifier.padding(15.dp))
                       Text((totalFinished).toString(), fontSize = 20.sp, modifier = Modifier.padding(horizontal = 55.dp, vertical = 20.dp))
                   }
               }

           }

    }





    @Composable
    fun SignInScreen(state:SignInState,onSignInClick:()-> Unit){
        val context = LocalContext.current
        LaunchedEffect(key1 = state.signInError){
           state.signInError?.let{
               Toast.makeText(context,it,Toast.LENGTH_LONG).show()
           }
        }
        val signInResult by viewModel.result.collectAsStateWithLifecycle()
        Row() {
            var name = "Click to Login"
            var imageUrl = "https://icons8.com/icon/zYQDIb6UB5Pk/test-account.png"
            var isSignInSuccessFul = false
            userData = googleAuthUIClient?.getSignedInUser()
            if (state.isSuccessFull || userData!= null) {
                isSignInSuccessFul = true
               // userData = signInResult.data

                userData?.let {
                    imageUrl = it.profilePic.toString()
                    name = it.userName.toString()
                }

            }
            IconButton(onClick =
            {
                onSignInClick.invoke()
            }) {
                if (isSignInSuccessFul) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = "profile pic", modifier = Modifier
                            .wrapContentHeight()
                            .wrapContentSize()
                            .wrapContentWidth()
                            .clip(CircleShape)
                    )
                }
                else{
                    Icon(painterResource(R.drawable.baseline_person_24), contentDescription = "Profile pic",
                        modifier = Modifier
                            .size(30.dp)
                            .border(shape = CircleShape, width = 1.dp, color = Color.Black))

                }

            }
            Column() {
                Text(text = name,modifier = Modifier.padding(top = 15.dp))
            }
        }
        }
    }



    @Preview
    @Composable
    fun previewCard(){
        Row {
            Canvas(modifier = Modifier
                .size(20.dp)
                .padding(3.dp) ){
                drawCircle(color = Color.Red)
            }
            Text(text = "All Tasks", fontSize = 15.sp)
        }



    }


