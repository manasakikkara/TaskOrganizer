package com.example.taskorganizer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.IntentSenderRequest.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.taskorganizer.signIn.GoogleAuthUIClient
import com.example.taskorganizer.signIn.SignInResult
import com.example.taskorganizer.signIn.SignInState
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MyProfileFragment: AllFragment() {

    private val viewModel:ActivityViewModel by viewModels(::requireActivity)
    private val googleAuthUIClient by lazy {
        val context = activity?.applicationContext
        context?.let {
            GoogleAuthUIClient(it, Identity.getSignInClient(it))
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         //super.onCreateView(inflater, container, savedInstanceState)
         return ComposeView(requireContext()).apply {
             setContent {
                 Column() {
                     Card(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(10.dp)
                     ) {

                         val state by viewModel.state.collectAsStateWithLifecycle()
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
                                 Toast.makeText(context,"signin successful",Toast.LENGTH_LONG).show()
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
        Row() {
            IconButton(onClick =
            {
                onSignInClick.invoke()
            }) {
                Icon(
                    painterResource(id = R.drawable.baseline_person_24),
                    "My Image"
                )
            }
            Column {
                Text("Click to Login")
            }
        }
    }

    @Preview
    @Composable
    fun previewCard(){
        Card(modifier = Modifier.fillMaxWidth()
        ) {


            Row() {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painterResource(id = R.drawable.baseline_person_24), "My Image")
                }
                Column {
                    Text("Click to Login")
                }
            }
        }
    }


}