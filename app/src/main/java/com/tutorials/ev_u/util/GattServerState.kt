package com.tutorials.ev_u.util

import android.bluetooth.BluetoothDevice

sealed class GattServerState {
    object Idle : GattServerState()
    object Connecting : GattServerState()
    object Disconnecting : GattServerState()
    data class Connected(val bluetoothDevice: BluetoothDevice) : GattServerState()
    object Disconnected : GattServerState()
}