package com.tutorials.ev_u

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tutorials.ev_u.databinding.FragmentDeviceConnectionBinding
import com.tutorials.ev_u.databinding.FragmentLogsBinding

class LogsFragment : Fragment() {
    private var _binding: FragmentLogsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            logTitle.setOnClickListener {
                val action = LogsFragmentDirections.actionLogsFragmentToLogDeviceDetailFragment(null)
                findNavController().navigate(action)
            }
            backBtn.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}