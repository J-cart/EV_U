package com.tutorials.bluetooth_one.serviceop

import android.bluetooth.BluetoothDevice
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlueDevice(val device: BluetoothDevice):Parcelable