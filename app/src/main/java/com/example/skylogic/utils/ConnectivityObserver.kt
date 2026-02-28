package com.example.skylogic.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.skylogic.utils.NetworkState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ConnectivityObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    fun observe(): Flow<NetworkState> = callbackFlow {

        fun checkCurrentState() {
            val network = connectivityManager.activeNetwork
            val capabilities =
                connectivityManager.getNetworkCapabilities(network)

            val isConnected =
                capabilities?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                ) == true

            trySend(
                if (isConnected)
                    NetworkState.Available
                else
                    NetworkState.Unavailable
            )
        }

        // check immediately
        checkCurrentState()

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                checkCurrentState()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                checkCurrentState()
            }

            override fun onLost(network: Network) {
                trySend(NetworkState.Unavailable)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}