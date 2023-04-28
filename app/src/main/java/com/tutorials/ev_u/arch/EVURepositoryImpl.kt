package com.tutorials.ev_u.arch

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tutorials.ev_u.util.RequestState
import com.tutorials.ev_u.util.EV_U_TAG
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EVURepositoryImpl : EVURepository {
    private val fAuth = Firebase.auth


    override fun signUp(
        fName: String,
        lName: String,
        email: String,
        password: String
    ): Flow<RequestState> {
        return callbackFlow {
            try {
                val signUp = fAuth.createUserWithEmailAndPassword(email, password).await()
                trySend(RequestState.Successful(true))
                Log.d(EV_U_TAG, "SUCCESS ALl SIGN UP TRANSACTION COMPLETED")
            } catch (e: Exception) {
                trySend(RequestState.Failure("${e.message}"))
                Log.d(EV_U_TAG, "SIGN UP ERROR--->$e")
            }
            awaitClose()
        }
    }

    override fun login(email: String, password: String): Flow<RequestState> {
        return callbackFlow {
            try {
                fAuth.signInWithEmailAndPassword(email, password).await()
                trySend(RequestState.Successful(true))
                Log.d(EV_U_TAG, "SUCCESS LOGIN OK")
            } catch (e: Exception) {
                trySend(RequestState.Failure("${e.message}"))
                Log.d(EV_U_TAG, "LOGIN ERROR--->$e")
            }
            awaitClose()
        }
    }


}