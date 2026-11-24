package com.example.tuan10

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log

class MyService : Service() {
    private lateinit var player: MediaPlayer
    private var uri: Uri? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        uri = intent?.let { intent.data }
        uri?.let {
            player = MediaPlayer.create(this, it)
            player.start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? = null
}
