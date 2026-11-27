package com.example.tuan10

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tuan10.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var playStatus = true
    private var myService : MyService?=  null
    private var isBound =  false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService.LocalBinder
            myService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            myService = null
        }
    }
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Bind service
        val intent = Intent(this, MyService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
        myService?.pause()
        //Xử lí sự kiện nút bật phát
        binding.btnPlayPause.setOnClickListener {
            val service = myService ?: return@setOnClickListener
            if(playStatus){
                service.pause()
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            }
            else{
                service.playOrResume()
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
            playStatus = !playStatus
        }
    }
}