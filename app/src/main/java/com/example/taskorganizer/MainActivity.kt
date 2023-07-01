package com.example.taskorganizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setHorizontalList()
            setBottomNavigation()
        }
    }


    @Composable
     fun setHorizontalList() {
        var list = listOf("All","Work","Personal","Wishlist","Birthday")
        LazyRow(userScrollEnabled = )
    }


    @Composable
 fun setBottomNavigation(){
     var isTasksClicked by remember { mutableStateOf(false) }
     var isCalendarClicked by remember { mutableStateOf(false) }
     var isProfileClicked by remember { mutableStateOf(false) }
     var isMenuClicked by remember { mutableStateOf(false) }
     Box(modifier = Modifier.fillMaxSize()) {

         NavigationBar(modifier = Modifier
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
         }


         BottomAppBar(modifier = Modifier
             .fillMaxWidth()
             .align(alignment = Alignment.BottomEnd), containerColor = Color.Gray, content = {
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

 }

    @Preview
    @Composable
    fun previewScreen(){
        setBottomNavigation()
    }
    
}


