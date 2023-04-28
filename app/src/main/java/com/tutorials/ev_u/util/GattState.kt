package com.tutorials.ev_u.util

import android.bluetooth.BluetoothGatt

sealed class GattState {
    object Idle : GattState()
    object Connecting : GattState()
    object Disconnecting : GattState()
    data class Connected(val gatt: BluetoothGatt) : GattState()
    object Disconnected : GattState()
}