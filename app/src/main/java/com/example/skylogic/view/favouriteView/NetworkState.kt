package com.example.skylogic.view.favouriteView

sealed class NetworkState {
    object Available : NetworkState()
    object Unavailable : NetworkState()
}