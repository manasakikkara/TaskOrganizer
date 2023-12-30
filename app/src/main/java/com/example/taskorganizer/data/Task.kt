package com.example.taskorganizer.data


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "Tasks")
data class Task(@PrimaryKey(autoGenerate = true)
                val id:Int, var taskName:String, /*var taskDate: String,*/
                var status:Status, var category:Category):Parcelable{
}


enum class Category{All,Work,Personal,WishList,Birthday}

enum class Status{Started,Completed}

