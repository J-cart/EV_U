package com.tutorials.ev_u.arch

import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tutorials.ev_u.util.RequestState
import com.tutorials.ev_u.util.Resource
import com.tutorials.bluetooth_one.serviceop.BlueDevice
import com.tutorials.bluetooth_one.serviceop.BluetoothObject
import com.tutorials.ev_u.util.BluetoothState
import com.tutorials.ev_u.util.GattState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
@RequiresApi(Build.VERSION_CODES.M)
class EVUViewModel : ViewModel() {
    private val repository = EVURepositoryImpl()

    var signUpState = MutableStateFlow<RequestState>(RequestState.NonExistent)
        private set
    var loginState = MutableStateFlow<RequestState>(RequestState.NonExistent)
        private set

    var authState = MutableStateFlow(true)
        private set

    var scannedDeviceResultFlow = MutableStateFlow<Resource<List<BlueDevice>>>(Resource.Failure("Click to start scanning..."))
        private set
     var bluetoothStateFlow = MutableStateFlow<BluetoothState>(BluetoothState.OFF)
        private set




    val gattState = BluetoothObject.gattCallBackFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),
        GattState.Idle)
//    val gattServerState = BluetoothObject.gattServerCallBackFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(),
//        GattServerState.Idle)

    fun signUp(fName: String, lName: String, email: String, password: String) {
        signUpState.value = RequestState.Loading
        viewModelScope.launch {
            repository.signUp(fName, lName, email, password).collect {
                signUpState.value = it
            }
        }
    }

    fun login(email: String, password: String) {
        loginState.value = RequestState.Loading
        viewModelScope.launch {
            repository.login(email, password).collect {
                loginState.value = it
            }

        }
    }

    fun toggleAuthState(state: Boolean) {
        authState.value = state
    }

    fun toggleSignUpState(state: RequestState){
        signUpState.value = state
    }

    fun toggleLoginState(state: RequestState){
        loginState.value = state
    }

    fun toggleScannedDeviceResultState(state: Resource<List<BlueDevice>>){
        scannedDeviceResultFlow.value = state
    }

     fun addToScanDeviceResultList(bluetoothDevice: BluetoothDevice) {
         if (scannedDeviceResultFlow.value is Resource.Successful){
             val exists = scannedDeviceResultFlow.value.data?.find {
                 it.device == bluetoothDevice
             }
             if (exists == null) {
                 val newList = scannedDeviceResultFlow.value.data?.toMutableList()
                 newList?.add(BlueDevice(bluetoothDevice))
                 scannedDeviceResultFlow.value = Resource.Successful(newList)
             }
             return
         }
         scannedDeviceResultFlow.value = Resource.Successful(listOf(BlueDevice(bluetoothDevice)))
    }

    fun toggleBluetoothState(state:BluetoothState){
        bluetoothStateFlow.value = state
    }

    fun signOutOp() {
        loginState.value = RequestState.NonExistent
        signUpState.value = RequestState.NonExistent
        Firebase.auth.signOut()
    }

}
