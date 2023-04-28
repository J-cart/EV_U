package com.tutorials.ev_u

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tutorials.bluetooth_one.serviceop.BluetoothObject
import com.tutorials.bluetooth_one.serviceop.BluetoothObject.connectToDevice
import com.tutorials.bluetooth_one.serviceop.BluetoothObject.startGatt
import com.tutorials.ev_u.databinding.FragmentDeviceConnectionBinding
@RequiresApi(Build.VERSION_CODES.M)
class DeviceConnectionFragment : Fragment() {
    private var _binding: FragmentDeviceConnectionBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DeviceConnectionFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDeviceConnectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bondStateIntentFilter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        requireContext().registerReceiver(bluetoothBondStateReceiver,bondStateIntentFilter)
        val deviceArgs = args.args
        deviceArgs?.let {
            binding.apply {
                deviceNameTv.text = deviceArgs.device.name ?:"DEVICE"
                deviceAddressTv.text = deviceArgs.device.address
                voltageTv.text = "%.2f".format(34.0)
                currentTv.text = "%.2f".format(35.0)
                socTv.text = "74%"
                capacityTv.text = "%.2f".format(36.0)
                connectBtn.setOnClickListener {
                    startGatt(requireContext())
                    connectToDevice(requireContext(),deviceArgs.device)
                }
            }
        }
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(bluetoothBondStateReceiver)
    }

    private val bluetoothBondStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent) {
            val state = p1.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
            when (state) {
                BluetoothDevice.BOND_BONDED -> {
                    Log.d(BluetoothObject.BLUE_TAG, "onReceive: BondState: BONDED")
                    binding.connectionStatusTv.text = "CONNECTED"

                }
                BluetoothDevice.BOND_BONDING -> {
                    Log.d(BluetoothObject.BLUE_TAG, "onReceive: BondState: BONDING")
                    binding.connectionStatusTv.text = "CONNECTING"
                }
                BluetoothDevice.BOND_NONE -> {
                    Log.d(BluetoothObject.BLUE_TAG, "onReceive: BondState: NONE")
                    binding.connectionStatusTv.text = "NOT CONNECTED"
                }

            }
        }
    }

}