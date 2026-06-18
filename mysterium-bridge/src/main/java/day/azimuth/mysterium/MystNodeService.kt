package day.azimuth.mysterium

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MystNodeService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var manager: MystNodeManagerImpl

    override fun onCreate() {
        super.onCreate()
        manager = MystNodeManagerImpl(applicationContext)
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> scope.launch { manager.start() }
            ACTION_STOP -> scope.launch {
                manager.stop()
                stopSelf(startId)
            }
            else -> stopSelf(startId)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (::manager.isInitialized) {
            runBlocking(Dispatchers.IO) {
                runCatching { manager.stop() }.onFailure { e ->
                    Log.e(TAG, "Shutdown failed in onDestroy", e)
                }
            }
        }
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Network Support",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Shows when the Network Support provider node is active"
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Network Support Active")
            .setContentText("Contributing bandwidth to the Azimuth network")
            .setSmallIcon(android.R.drawable.ic_menu_share)
            .setOngoing(true)
            .build()

    companion object {
        const val ACTION_START = "day.azimuth.mysterium.START"
        const val ACTION_STOP = "day.azimuth.mysterium.STOP"
        private const val CHANNEL_ID = "mysterium_node_service"
        private const val NOTIFICATION_ID = 2
        private const val TAG = "MystNodeService"

        fun startNode(context: Context) {
            val intent = Intent(context, MystNodeService::class.java).apply {
                action = ACTION_START
            }
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start foreground service", e)
                throw e
            }
        }

        fun stopNode(context: Context) {
            val intent = Intent(context, MystNodeService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }
}
