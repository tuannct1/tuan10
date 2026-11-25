package com.example.tuan10

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tuan10.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var playStatus = true
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
        binding.btnPlayPause.setOnClickListener {
            if(playStatus){
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
            else{
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            }
            playStatus = !playStatus
        }
    }
}