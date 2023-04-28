package com.tutorials.ev_u.util

const val EV_U_TAG = "EV_U_TAG"
const val CURRENT_DESTINATION_ID = "current destination"



/* private val newGattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)

            when (status) {
                BluetoothGattServer.STATE_CONNECTING -> {
                    Log.d(BLUE_TAG, "onConnectionStateChange: gattServerCallBack connecting...")
                    gattServerCallBackFlow.value = GattServerState.Connecting
                }
                BluetoothGattServer.STATE_CONNECTED -> {
                    Log.d(BLUE_TAG, "onConnectionStateChange: gattServerCallBack CONNECTED")
                    gattServerCallBackFlow.value = GattServerState.Connected(device)
                }
                BluetoothGattServer.STATE_DISCONNECTING -> {
                    Log.d(BLUE_TAG, "onConnectionStateChange: gattServerCallBack disconnecting...")
                    gattServerCallBackFlow.value = GattServerState.Disconnecting
                }
                BluetoothGattServer.STATE_DISCONNECTED -> {
                    Log.d(BLUE_TAG, "onConnectionStateChange: gattServerCallBack DISCONNECTED...")
                    gattServerCallBackFlow.value = GattServerState.Disconnected
                }
            }

        }

        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            super.onServiceAdded(status, service)
        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
        }

        override fun onDescriptorReadRequest(
            device: BluetoothDevice?,
            requestId: Int,
            offset: Int,
            descriptor: BluetoothGattDescriptor?
        ) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor)
        }

        override fun onDescriptorWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            descriptor: BluetoothGattDescriptor?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onDescriptorWriteRequest(
                device,
                requestId,
                descriptor,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
        }

    }
        private fun getGattService(): BluetoothGattService {
        val service = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        val messageCharacteristics = BluetoothGattCharacteristic(
            MESSAGE_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        // read only characteristics
        /*val messageCharacteristics = BluetoothGattCharacteristic(
            MESSAGE_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )*/
        val configDescriptor = BluetoothGattDescriptor(
            CLIENT_CONFIG_UUID,
            BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE
        )
        messageCharacteristics.addDescriptor(configDescriptor)
        service.addCharacteristic(messageCharacteristics)
        return service
    }
    */