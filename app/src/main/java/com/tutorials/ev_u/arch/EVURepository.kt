package com.tutorials.ev_u.arch

import com.tutorials.ev_u.util.RequestState
import kotlinx.coroutines.flow.Flow

interface EVURepository {
    fun signUp(fName:String,lName:String,email:String,password:String): Flow<RequestState>
    fun login(email:String,password:String): Flow<RequestState>

}