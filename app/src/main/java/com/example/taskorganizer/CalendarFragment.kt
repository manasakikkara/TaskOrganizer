package com.example.taskorganizer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskorganizer.data.Task
import com.example.taskorganizer.databinding.AllFragmentBinding
import kotlinx.coroutines.launch

class CalendarFragment: Fragment() {

    private val viewModel:ActivityViewModel by viewModels(::requireActivity)
    var isLoadingDone by mutableStateOf(false)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         //super.onCreateView(inflater, container, savedInstanceState)
         return ComposeView(requireContext()).apply {
             setContent {
                 Column() {
                     loadCalendarView()
                     loadAllFragmentView()
                 }
             }
         }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun loadCalendarView() {
        val datePickerState = rememberDatePickerState()
        DatePicker(
            state = datePickerState,
            modifier = Modifier.padding(),
            title =
            null
            , headline = null, showModeToggle = false)
        viewModel.convertMillisToDate(datePickerState.selectedDateMillis)
        var coroutineScope = rememberCoroutineScope()

        coroutineScope.launch {
            getTasksOnDateSelected()

        }
    }


    @Composable
    private fun loadAllFragmentView() {
        var removeTaskListener = object : ActivityViewModel.RemoveTaskListener {
            override suspend fun removeTask(task: Task) {
                viewModel.deleteTask(task = task, object : ActivityViewModel.DeleteListener {
                    override suspend fun onTaskDeleted() {
                        getTasksOnDateSelected()
                    }

                })
            }

            override suspend fun updateTaskAsFinished(task: Task) {
                viewModel.updateTask(task = task, object : ActivityViewModel.UpdateListener {
                    override suspend fun onTaskUpdated() {
                        getTasksOnDateSelected()

                    }

                })
            }

        }

        var instance = AllFragment()
        var bundle = Bundle()
        bundle.putParcelableArrayList("tasksList", ArrayList(viewModel.list))
        instance.arguments = bundle
        instance.setOnClickListener(removeTaskListener)
        AndroidViewBinding(AllFragmentBinding::inflate) {
            childFragmentManager.beginTransaction().setReorderingAllowed(true)
                .replace(R.id.fragment_all, instance).commit()
        }

    }

    private suspend fun getTasksOnDateSelected() {
        viewModel.getTasksOfTheDateSelected(
            tasksListListener = object : ActivityViewModel.TasksListListener {
                override fun onTasksDone() {
                    isLoadingDone = true
                }

            })
    }


}