/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.taskorganizer.data

import android.content.Context
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.taskorganizer.repository.OfflineTaskRepository
import com.example.taskorganizer.repository.TaskRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val taskRepository: TaskRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineTaskRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [taskRepository]
     */
    override val taskRepository: TaskRepository by lazy {


        OfflineTaskRepository(TaskDataBase.getDataBaseInstance(context).taskDao())
    }
}
