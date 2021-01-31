package com.moskofidi.fintech

import android.app.Application
import android.content.Context
import com.moskofidi.fintech.listener.ConnectionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DevApplication : Application() {
    companion object {
        private var isConnected = true
        private var connectionListener: ConnectionListener? = null

        fun setConnectionListener(context: Context) {
            connectionListener = ConnectionListener(context)
        }

        fun getNetworkState() : Boolean {
            return isConnected
        }

        fun regListener() {
            connectionListener?.registerConnectionListener()
        }

        fun unregisterListener() {
            connectionListener?.unregisterConnectionListener()
        }

        fun setNetworkState() {
            connectionListener?.result = { isAvailable ->
                when (isAvailable) {
                    true -> {
                        isConnected = true
                    }
                    false -> {
                        isConnected = false
                    }
                }
            }

        }
    }
}