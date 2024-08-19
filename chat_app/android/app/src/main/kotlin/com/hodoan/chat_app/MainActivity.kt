package com.hodoan.chat_app

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationManagerCompat
import io.flutter.Log
import io.flutter.embedding.android.FlutterActivity
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.Socket


class MainActivity : FlutterActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, PushNotificationService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}

class PushNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("PushNotificationReceiver", " ========= Broadcast received")

        val serverIp = "10.50.10.93"
        val port = 12345
        val deviceId = "your_device_id" // Replace with unique device ID

        val thread = Thread {
            android.util.Log.e("///////", "onReceive: start thread")
            try {
                val socket = Socket(serverIp, port)
                val writer = OutputStreamWriter(socket.getOutputStream())

                // Register device
                val registerMessage = JSONObject()
                registerMessage.put("action", "register")
                registerMessage.put("deviceId", deviceId)
                writer.write(registerMessage.toString())
                writer.flush()

                var str = ""
                while (true) {
                    val data = socket.getInputStream().read()
                    str += data.toChar()
                    if (socket.getInputStream().available() == 0) {
                        android.util.Log.e("///", "onReceive: $str")
                        str = ""
                    }
                }
            } catch (e: Exception) {
                Log.e("PushNotificationReceiver", "Socket communication error", e)
            }
        }

        thread.start()
    }
}

class PushNotificationService : Service() {
    private val context = this
    private var notifyManagerId = 1

    override fun onCreate() {
        super.onCreate()
        startForeground()
    }

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }
        createSocket()
    }

    private fun connectSocket() {
        try {
            val serverIp = "10.50.10.93"
            val port = 12345
            val deviceId = "your_device_id" // Replace with unique device ID

            val socket = Socket(serverIp, port)
            val writer = OutputStreamWriter(socket.getOutputStream())

            // Register device
            val registerMessage = JSONObject()
            registerMessage.put("action", "register")
            registerMessage.put("deviceId", deviceId)
            writer.write(registerMessage.toString())
            writer.flush()

            var str = ""
            while (true) {
                val data = socket.getInputStream().read()
                str += data.toChar()
                if (socket.getInputStream().available() == 0) {
                    android.util.Log.e("///", "onReceive: $str")
                    showNotification(str)
                    str = ""
                }
            }
        } catch (e: Exception) {
            Log.e("PushNotificationReceiver", "Socket communication error", e)
            Log.e("PushNotificationReceiver", "reconnect socket")
            Thread.sleep(5000)
            createSocket()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(str: String) {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(context)
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)

        val notification = notificationBuilder
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_DEFAULT)
            .setChannelId(channelId)
            .setContentTitle("New message")
            .setContentText(str)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(notifyManagerId, notification)
            notifyManagerId++
        }

        startForeground(101, notification)
    }

    private fun createSocket() {

        val thread = Thread {
            android.util.Log.e("///////", "onReceive: start thread")
            connectSocket()
        }

        thread.start()
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun createNotificationChannel(context: Context): String {
            val channelId = "com.hodoan.chat_app"
            val channelName = "My Background Service"
            val chan = NotificationChannel(
                channelId,
                channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
//        chan.lightColor = Color.BLUE
//        chan.importance = NotificationManager.IMPORTANCE_DEFAULT
//        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
            return channelId
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }
}