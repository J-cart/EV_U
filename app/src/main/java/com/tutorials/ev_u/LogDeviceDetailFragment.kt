package com.tutorials.ev_u

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tutorials.bluetooth_one.serviceop.BluetoothObject
import com.tutorials.ev_u.databinding.FragmentLogDeviceDetailBinding

class LogDeviceDetailFragment : Fragment() {
    private var _binding: FragmentLogDeviceDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLogDeviceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

            voltageTv.text = "%.2f".format(34.0)
            currentTv.text = "%.2f".format(35.0)
            socTv.text = "74%"
            capacityTv.text = "%.2f".format(36.0)
        }

    }
}