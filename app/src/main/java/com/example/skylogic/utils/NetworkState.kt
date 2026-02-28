package com.example.skylogic.utils

sealed class NetworkState {
    object Available : NetworkState()
    object Unavailable : NetworkState()
}