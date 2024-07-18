package com.example.taskorganizer.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat.Flags
import androidx.core.content.ContextCompat
import com.example.taskorganizer.MainActivity
import com.example.taskorganizer.R
import kotlin.random.Random

class TaskNotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java) as NotificationManager

    fun showBasicNotification(){
        var pendingIntent = PendingIntent.getActivity(context,204,
            Intent(context,MainActivity::class.java).putExtra("FromNotifications",true),PendingIntent.FLAG_IMMUTABLE)
        val notificationMessage = NotificationCompat.Builder(context,"taskNotification")
            .setContentTitle("Add a Task")
            .setContentText("Tap to add a task let productivity unmask")
            .setSmallIcon(R.drawable.todoicon)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify("taskNotification", Random.nextInt(),notificationMessage)
    }

    //To convert drawable to bitmap
    fun convertImageToBitmap(drawable:Int):Bitmap?{
        var image  = ContextCompat.getDrawable(context,drawable)


         var   bitmap =  Bitmap.createBitmap(image!!.intrinsicWidth,image!!.intrinsicHeight,Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        image.setBounds(0,0,canvas.width,canvas.height)
        image.draw(canvas)
        return bitmap
    }
}