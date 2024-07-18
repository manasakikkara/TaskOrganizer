package com.example.taskorganizer.signIn

import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class SignInResult(val data: UserData?, val errormsg:String?)

data class UserData(var userId:String,var userName:String?,var profilePic:String?)
