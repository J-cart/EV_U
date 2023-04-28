package com.tutorials.ev_u

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.ParcelUuid
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tutorials.ev_u.util.Resource
import com.tutorials.bluetooth_one.serviceop.BluetoothObject
import com.tutorials.bluetooth_one.serviceop.BluetoothObject.BLUE_TAG
import com.tutorials.ev_u.arch.EVUViewModel
import com.tutorials.ev_u.databinding.FragmentHomeBinding
import com.tutorials.ev_u.serviceop.BluetoothDeviceListAdapter
import com.tutorials.ev_u.util.BluetoothState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.N)
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<EVUViewModel>()

    private val bluetoothManager by lazy { requireContext().getSystemService(BluetoothManager::class.java) }

    //  private val scannedResultList = mutableListOf<BlueDevice>()
    private val scanResultListAdapter = BluetoothDeviceListAdapter()

    private var exitAppToastStillShowing = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            isEnabled = true
            exitApp()
        }
        binding.scanRecyclerView.adapter = scanResultListAdapter
        val switchIntentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        val discoveryIntentFilter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(bluetoothSwitchReceiver, switchIntentFilter)
        requireActivity().registerReceiver(bluetoothDeviceDiscoveryReceiver, discoveryIntentFilter)

        bluetoothInitialization()
        observeScannedDeviceResult()
        observeBluetoothState()
        requestPermissions(permissionsResultLauncher)
        scanResultListAdapter.adapterClick {
            val action = HomeFragmentDirections.actionHomeFragmentToDeviceConnectionFragment(it)
            findNavController().navigate(action)
        }
        binding.apply {
            logBtn.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToLogsFragment()
                findNavController().navigate(action)
            }
            moreBtn.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToAccountProfileFragment()
                findNavController().navigate(action)
            }



        }
    }


    private fun bluetoothInitialization() {
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            Toast.makeText(
                requireContext(),
                "Bluetooth not supported",
                Toast.LENGTH_SHORT
            ).show()
           viewModel.toggleBluetoothState(BluetoothState.NOT_SUPPORTED)
            Log.d(BLUE_TAG, "bluetoothInitialization: Bluetooth not supported")
            return
        }

        if (!bluetoothAdapter.isEnabled){
            viewModel.toggleBluetoothState(BluetoothState.OFF)
        }else{
            viewModel.toggleBluetoothState(BluetoothState.ON)
        }

    }

    private val bluetoothSwitchResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.d(BLUE_TAG, "resultContract: Bluetooth Switched ON")
                viewModel.toggleBluetoothState(BluetoothState.ON)
                return@registerForActivityResult
            }
            Log.d(
                BLUE_TAG,
                "resultContract: Bluetooth Not ON , some error occurred "
            )
            viewModel.toggleBluetoothState(BluetoothState.OFF)
        }

    private fun checkPermissions(): Boolean {
        val fineLocation = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val phoneState = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation && phoneState


    }

    private fun requestPermissions(permissionsResultLauncher: ActivityResultLauncher<Array<String>>) {
        when (checkPermissions()) {
            true -> {
                Log.d(
                    BLUE_TAG,
                    "Permissions: Some or Most permission available--> Initial checking"
                )
                binding.bluetoothSwitchBtn.setOnClickListener {
                    bluetoothInitialization()
                }
            }
            false -> {
                Log.d(
                    BLUE_TAG,
                    "Permissions: Some or No permission available--> Initial checking"
                )
                permissionsResultLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.BLUETOOTH
                    )
                )
            }
        }

    }


    private val permissionsResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
                        || it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Log.d(BLUE_TAG, "Permissions: Fine Location Accepted")
                    bluetoothInitialization()
                }

                it.getOrDefault(Manifest.permission.BLUETOOTH, false) -> {
                    Log.d(BLUE_TAG, "Permissions: Bluetooth Accepted")
                    bluetoothInitialization()
                }
                it.getOrDefault(Manifest.permission.BLUETOOTH_ADMIN, false) -> {
                    Log.d(BLUE_TAG, "Permissions: Bluetooth ADMIN Accepted")
                }
                it.getOrDefault(Manifest.permission.BLUETOOTH_ADVERTISE, false)
                        || it.getOrDefault(Manifest.permission.BLUETOOTH_CONNECT, false) ||
                        it.getOrDefault(Manifest.permission.BLUETOOTH_SCAN, false) -> {
                    Log.d(
                        BLUE_TAG,
                        "Permissions: Bluetooth Miscellaneous accepted "
                    )
                }
                else -> {
                    Log.d(BLUE_TAG, "Permissions: Well... else case")

                }
            }
        }

    private val bluetoothSwitchReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent) {
            val state = p1.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
            when (state) {
                BluetoothAdapter.STATE_ON -> {
                    Log.d(BLUE_TAG, "bluetoothSwitchReceiver: Bluetooth ON")
                    viewModel.toggleBluetoothState(BluetoothState.ON)


                }
                BluetoothAdapter.STATE_TURNING_ON -> {
                    Log.d(BLUE_TAG, "bluetoothSwitchReceiver: Turning Bluetooth ON")
                    viewModel.toggleBluetoothState(BluetoothState.TURNING_ON)
                }
                BluetoothAdapter.STATE_OFF -> {
                    Log.d(BLUE_TAG, "bluetoothSwitchReceiver: Bluetooth OFF")
                    viewModel.toggleBluetoothState(BluetoothState.OFF)
                }
                BluetoothAdapter.STATE_TURNING_OFF -> {
                    Log.d(BLUE_TAG, "bluetoothSwitchReceiver: Turning Bluetooth OFF")
                    viewModel.toggleBluetoothState(BluetoothState.TURNING_OFF)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(bluetoothSwitchReceiver)
        requireActivity().unregisterReceiver(bluetoothDeviceDiscoveryReceiver)
    }

    //region SCANNING FOR DEVICES

    private fun startScanning() {
        val scanner = bluetoothManager.adapter.bluetoothLeScanner
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                Log.d(BLUE_TAG, "startScanning: starting...")
                binding.scanTv.text = "Scanning...."
                binding.progressBar.isVisible = true
            }
            viewModel.toggleScannedDeviceResultState(Resource.Loading())
//            scanner.startScan(mutableListOf(scanFilter), scanSettings, scannerCallBack)
            scanner.startScan(scannerCallBack)

            delay(10000L)
            Log.d(BLUE_TAG, "startScanning: stopping...")
            withContext(Dispatchers.Main) {
                binding.scanTv.text = " Scan for devices"
                binding.progressBar.isVisible = false
            }
            scanner.stopScan(scannerCallBack)
        }

    }

    private val scannerCallBack = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.d(BLUE_TAG, "onScanResult: ${result.toString()}")
            result?.let {
                viewModel.addToScanDeviceResultList(it.device)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.d(BLUE_TAG, "onBatchScanResults: ${results.toString()}")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d(BLUE_TAG, "onScanFailed: $errorCode ")
        }
    }

    private val scanFilter = ScanFilter.Builder()
        .setServiceUuid(ParcelUuid(BluetoothObject.SERVICE_UUID))
        .build()

    private val scanSettings: ScanSettings
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                scanSettingsSinceM
            } else {
                scanSettingsBeforeM
            }
        }

    private val scanSettingsBeforeM = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setReportDelay(0)
        .build()

    private val scanSettingsSinceM = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
        .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
        .setReportDelay(0)
        .build()


    //BLUETOOTH DISCOVERY

    private val bluetoothDeviceDiscoveryReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent) {
            when (p1.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    Log.d(BLUE_TAG, "onReceive: Discovery --> $device")
                    device?.let { bDevice ->
                        viewModel.addToScanDeviceResultList(bDevice)
                    }

                }
            }
        }
    }

    private fun observeScannedDeviceResult() {
        lifecycleScope.launch {
            viewModel.scannedDeviceResultFlow.collect { state ->
                when (state) {
                    is Resource.Successful -> {
                        binding.apply {
                            state.data?.let {
                                if (it.isEmpty()) {
                                    Log.d(
                                        BLUE_TAG,
                                        "observeScannedDeviceResult: Success but empty "
                                    )
                                    progressBar.isVisible = false
                                    emptyStateTv.isVisible = true
                                    scanRecyclerView.isVisible = false
                                } else {
                                    Log.d(
                                        BLUE_TAG,
                                        "observeScannedDeviceResult: Success --> ${state.data}"
                                    )
                                    progressBar.isVisible = false
                                    emptyStateTv.isVisible = false
                                    scanRecyclerView.isVisible = true

                                    scanResultListAdapter.submitList(state.data)
                                }
                            }

                        }

                    }
                    is Resource.Failure -> {
                        Log.d(BLUE_TAG, "observeScannedDeviceResult: Failed -${state.msg} ")
                        binding.apply {
                            progressBar.isVisible = false
                            emptyStateTv.isVisible = true
                            scanRecyclerView.isVisible = false

                            emptyStateTv.text = state.msg
                        }
                    }
                    is Resource.Loading -> {
                        Log.d(BLUE_TAG, "observeScannedDeviceResult: Loading ")
                        binding.apply {
                            progressBar.isVisible = true
                            emptyStateTv.isVisible = false
                            scanRecyclerView.isVisible = false
                        }
                    }
                }

            }
        }
    }

//endregion

    private fun observeBluetoothState(){
        lifecycleScope.launch{
            viewModel.bluetoothStateFlow.collect{state->
                when(state){
                    BluetoothState.OFF->{
                        binding.bluetoothStatusTv.text = "Bluetooth is OFF"
                        binding.bluetoothSwitchBtn.setOnClickListener {
                            bluetoothSwitchResultLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                        }
                        binding.scanImg.setOnClickListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Turn On Bluetooth to start scanning",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    BluetoothState.TURNING_OFF->{
                        binding.bluetoothStatusTv.text = "Turning Bluetooth OFF"
                    }
                    BluetoothState.ON->{
                        binding.bluetoothStatusTv.text = "Bluetooth is ON"
                        binding.scanImg.setOnClickListener {
                            startScanning()
//                            bluetoothManager.adapter.startDiscovery()
                        }
                        binding.bluetoothSwitchBtn.setOnClickListener {
                            bluetoothManager.adapter.disable()
                        }

                    }
                    BluetoothState.TURNING_ON->{
                        binding.bluetoothStatusTv.text = "Turning Bluetooth ON"
                    }
                    BluetoothState.NOT_SUPPORTED->{
                        binding.bluetoothStatusTv.text = "Bluetooth not supported"
                    }

                }

            }
        }
    }

    private val exitAppTimer = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            exitAppToastStillShowing = false
        }
    }
    private fun exitApp() {
        if (exitAppToastStillShowing) {
            requireActivity().finish()
            return
        }

        Toast.makeText(this.requireContext(), "Tap again to exit", Toast.LENGTH_SHORT)
            .show()
        exitAppToastStillShowing = true
        exitAppTimer.start()
    }


}


