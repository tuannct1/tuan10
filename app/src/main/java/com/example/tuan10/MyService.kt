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
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MyService : Service() {
    private var player: MediaPlayer? = null
    private var uri: Uri? = null

    inner class LocalBinder : Binder(){
        fun getService(): MyService = this@MyService
    }
    private val localBinder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMusicForeground()
        //Lấy uri rồi chạy bài hát
        uri = intent?.let { intent.data }
        uri?.let {
            player?.release()
            player = MediaPlayer.create(this, it)
            player?.start()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder = localBinder

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }
    fun playOrResume(){
        if(player?.isPlaying == false){
            player?.start()
        }
    }
    fun pause() {
        if (player?.isPlaying == true) {
            player?.pause()
        }
    }
    @SuppressLint("ForegroundServiceType")
    private fun startMusicForeground() {
        // Tạo channel cấu hình thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("music", "MusicApp", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Tạo thông báo
        val notification = NotificationCompat.Builder(this, "music")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Phát nhạc")
            .setOngoing(true)
            .build()

        // Khởi động Foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
    }
}
