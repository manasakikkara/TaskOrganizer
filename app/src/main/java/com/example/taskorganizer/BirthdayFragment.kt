package com.example.taskorganizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.taskorganizer.data.Task
import com.example.taskorganizer.databinding.AllFragmentBinding

class BirthdayFragment: AllFragment() {

    private var _binding:AllFragmentBinding?= null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         //super.onCreateView(inflater, container, savedInstanceState)
         return ComposeView(requireContext()).apply {
             setContent {
                 getListFromBundle(tasksList = arguments?.getParcelableArrayList<Task>("tasksList"))
             }
         }

    }


}