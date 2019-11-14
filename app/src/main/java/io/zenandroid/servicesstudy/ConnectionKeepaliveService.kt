package io.zenandroid.servicesstudy

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

private const val FOREGROUND_NOTIFICATION_ID = 798

class ConnectionKeepaliveService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.getBooleanExtra("DISCONNECT", false) == true) {
            ConnectionManager.disconnect()
            stopSelf()
        } else {
            startForeground(FOREGROUND_NOTIFICATION_ID, buildNotification())
        }
        return START_NOT_STICKY
    }

    private fun buildNotification() : Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val disconnectIntent = Intent(this, ConnectionKeepaliveService::class.java).putExtra("DISCONNECT", true)
        val disconnectPending = PendingIntent.getService(this, 1, disconnectIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, "NotificationChannel")
            .setContentTitle("Active connection")
            .setContentText("You have an active connection")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Disconnect", disconnectPending)
            .build()
    }

    override fun onDestroy() {
        Log.d("CKS", "Destroy service")
    }
}
