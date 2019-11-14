package io.zenandroid.servicesstudy

import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.*

const val TAG = "ConnectionManager"

object ConnectionManager: LifecycleObserver {

    var peristent: Boolean = false

    private var foreground = false
    private var pendingJob : Job? = null

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onBackground() {
        Log.d(TAG, "Going to background")

        foreground = false
        if(peristent) {
            Log.d(TAG, "Starting Foreground Service")
            ContextCompat.startForegroundService(App.INSTANCE, Intent(App.INSTANCE, ConnectionKeepaliveService::class.java))
        } else {
            Log.d(TAG, "Starting countdown")
            pendingJob = GlobalScope.launch(Dispatchers.Main) {
                delay(5_000)
                disconnect()
            }
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onForeground() {
        Log.d(TAG, "Going to foreground")

        if(pendingJob?.isActive == true) {
            pendingJob?.cancel()
            Log.d(TAG, "Cancelling countdown")
        }
        foreground = true
        App.INSTANCE.stopService(Intent(App.INSTANCE, ConnectionKeepaliveService::class.java))
    }

    fun disconnect() {
        Log.d(TAG, "Closing connection")
    }

}