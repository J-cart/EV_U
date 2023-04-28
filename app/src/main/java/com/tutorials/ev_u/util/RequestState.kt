package com.tutorials.ev_u.util

sealed class RequestState {
    class Successful(val data: Boolean) : RequestState()
    class Failure(val msg: String) : RequestState()
    object NonExistent : RequestState()
    object Loading : RequestState()
}