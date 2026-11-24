package com.example.tuan10

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tuan10.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var adapter: MyAdapter
    private var songList: List<Song> = emptyList()
    private var currentSong: Song? = null

     private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                binding.layoutPermission.visibility = View.GONE
                loadAndShowSongs()
            } else {
                binding.layoutPermission.visibility = View.VISIBLE
                Toast.makeText(this, "Cần cấp quyền để hiển thị nhạc trong máy", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Khởi tạo dapter
        adapter = MyAdapter(
            songList = songList,
            onItemClick = { selectedSong ->
                openPlayerScreen(selectedSong)
            },
            onMoreClick = { selectedSong ->
                Toast.makeText(this, "Bài hát: ${selectedSong.title}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvSongs.adapter = adapter
        //Kiểm tra version và lấy permission
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        //Kiểm tra quyền
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            binding.layoutPermission.visibility = View.GONE
            loadAndShowSongs()
        }
        //Set sự kiện cho xin quyền
        binding.btnGrantPermission.setOnClickListener {
            requestPermissionLauncher.launch(permission)
        }
    }
    private fun openPlayerScreen(song: Song) {
        currentSong = song
        // Cập nhật tên bài hát lên thanh Mini Player
        binding.tvMiniTitle.text = song.title
        binding.tvMiniArtist.text = song.artist

        // Chuyển sang màn hình phát nhạc đĩa xoay (MainActivity)
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("SONG_TITLE", song.title)
            putExtra("SONG_ARTIST", song.artist)
            putExtra("SONG_PATH", song.path)
        }
        startActivity(intent)
    }

    private fun loadAndShowSongs() {
        songList = loadSongsFromDevice()
        adapter.updateSongs(songList)
        binding.tvSongCount.text = "${songList.size} bài hát"
    }

    private fun loadSongsFromDevice(): List<Song> {
        val songList = mutableListOf<Song>()
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )

        // Chỉ lấy file nhạc thực sự và có độ dài >= 10 giây (10.000 ms)
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 10000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        contentResolver.query(collection, projection, selection, null, sortOrder)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val title = cursor.getString(titleCol) ?: "Không rõ tên bài hát"
                val rawArtist = cursor.getString(artistCol)
                val artist = if (rawArtist.isNullOrBlank() || rawArtist == "<unknown>") {
                    "Nghệ sĩ không xác định"
                } else {
                    rawArtist
                }

                // Tạo Uri chuẩn để MediaPlayer phát được trên mọi đời Android
                val audioUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                songList.add(
                    Song(
                        title = title,
                        artist = artist,
                        path = audioUri.toString()
                    )
                )
            }
        }
        return songList
    }
}