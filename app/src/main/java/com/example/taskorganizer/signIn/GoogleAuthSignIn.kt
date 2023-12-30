package com.example.taskorganizer.signIn

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.taskorganizer.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.concurrent.CancellationException

class GoogleAuthUIClient( val context: Context,val oneTapClient:SignInClient) {

    private val auth = Firebase.auth

    //begining the signin
    suspend fun signIn():IntentSender?{
        val result =  try{
           oneTapClient.beginSignIn(buildSignInRequest()).await()
        }
        catch (e:Exception){
            e.printStackTrace()
            if(e is CancellationException) {
              throw e
            }
            else null
        }
        return result?.pendingIntent?.intentSender

    }


    //firebase sdk handles the results from the intentsender and deserialises the response
    //we now need a function to handle the intent thrown back to the app
    @SuppressLint("SuspiciousIndentation")
    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken,null)
              return  try{
                    val user = auth.signInWithCredential(googleCredentials).await().user
                  SignInResult(data =
                  user?.run{
                      UserData(userId = uid, userName = displayName, profilePic = photoUrl?.toString())
                  }
                      , errormsg = null)
                }
                catch(e:Exception){
                    e.printStackTrace()
                    if(e is CancellationException) throw e
                    else{
                        SignInResult(null,e.message)
                    }
                }

    }

    //  Building the request(a typical builder)
    suspend fun buildSignInRequest():BeginSignInRequest{
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_Id)).build())
            .setAutoSelectEnabled(true)
            .build()
    }

    suspend fun signOut(){
        try{
          oneTapClient.signOut().await()
          auth.signOut()
        }
        catch (e:Exception){
            e.printStackTrace()
            if(e is CancellationException){
                throw e
            }
        }
    }

    suspend fun getSignedInUser(): UserData? {
         return auth.currentUser?.run{
             UserData(userId = uid, userName = displayName, profilePic = photoUrl.toString())
        }
    }
}