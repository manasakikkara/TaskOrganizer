package com.example.taskorganizer.signIn

data class SignInResult(val data: UserData?, val errormsg:String?)

data class UserData(val userId:String,val userName:String?,val profilePic:String?)
