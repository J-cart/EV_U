package com.tutorials.bluetooth_one.serviceop

import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.tutorials.ev_u.util.GattState
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.M)
object BluetoothObject {
    const val BLUE_TAG = "BLUETOOTH_DEV"

    val SERVICE_UUID: UUID = UUID.fromString("0000b81d-0000-1000-8000-00805f9b34fb")
    private val CLIENT_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    val MESSAGE_UUID: UUID = UUID.fromString("7db3e235-3608-41f3-a03c-955fcbd2ea4b")

    var gattCallBackFlow = MutableStateFlow<GattState>(GattState.Idle)
        private set

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private var mainGatt: BluetoothGatt? = null

    private var gattClient: BluetoothGatt? = null
    //private var gattClientCallBack:BluetoothGattCallback? = null

    private var messageCharacteristics: BluetoothGattCharacteristic? = null


    //private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    /**
     * 1. start server
     * 2. setUpGattServer*/

    fun startGatt(context: Context) {
        bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
    }


    fun connectToDevice(context: Context, bluetoothDevice: BluetoothDevice) {
        val device = bluetoothAdapter.getRemoteDevice(bluetoothDevice.address)
        gattClient = device.connectGatt(context, false, newGattClientCallBack)
    }

    private val newGattClientCallBack = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val isSuccessful = status == BluetoothGatt.GATT_SUCCESS
            val isConnected = newState == BluetoothGatt.STATE_CONNECTED

            if (isSuccessful && isConnected) {
                // do something
                mainGatt = gatt
                gatt.discoverServices()
            }

            when (status) {
                BluetoothGatt.STATE_CONNECTING -> {
                    Log.d(BLUE_TAG, "onConnectionStateChange: gattCallBack connecting...")
                    gattCallBackFlow.value = GattState.Connecting
                }
                BluetoothGatt.STATE_CONNECTED -> {
                    Log.d(BLUE_TAG, "onConnectionStateChange: gattCallBack CONNECTED")
                    gattCallBackFlow.value = GattState.Connected(gatt)
                }
                BluetoothGatt.STATE_DISCONNECTING -> {
                    Log.d(BLUE_TAG, "onConnectionStateChange: gattCallBack disconnecting...")
                    gattCallBackFlow.value = GattState.Disconnecting
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    Log.d(BLUE_TAG, "onConnectionStateChange: gattCallBack DISCONNECTED...")
                    gatt.close()
                    mainGatt?.close()
                    mainGatt = null
                    gattCallBackFlow.value = GattState.Disconnected
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            val isSuccessful = status == BluetoothGatt.GATT_SUCCESS

            if (isSuccessful) {
                // do something
                mainGatt = gatt
                val service = gatt.getService(SERVICE_UUID)
                if (service != null) {
                    messageCharacteristics = service.getCharacteristic(MESSAGE_UUID).apply {
                        Log.d(BLUE_TAG, "onServicesDiscovered: chars--> $this")
                    }

                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
        }

        override fun onDescriptorRead(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorRead(gatt, descriptor, status)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
        }

        override fun onServiceChanged(gatt: BluetoothGatt) {
            super.onServiceChanged(gatt)
        }
    }





}