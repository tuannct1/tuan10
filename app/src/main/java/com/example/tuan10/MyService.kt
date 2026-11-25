package com.example.tuan10

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MyService : Service() {
    private lateinit var player: MediaPlayer
    private var uri: Uri? = null
    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Tạo channel cấu hình thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("music", "MusicApp", NotificationManager.IMPORTANCE_LOW)
            //Lấy service quản lý thông báo của hệ điều hành
            val manager = getSystemService(NotificationManager::class.java)
            //Đăng kí channle
            manager.createNotificationChannel(channel)
        }
        //Tạo thông báo
        val notification = NotificationCompat.Builder(this,"music")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Phát nhạc")
            .setOngoing(true)
            .build()
        //Khởi động Foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
        //Lấy uri rồi chạy bài hát
        uri = intent?.let { intent.data }
        uri?.let {
            if(player.isPlaying){
                player.release()
            }
            player = MediaPlayer.create(this, it)
            player.start()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null
}
