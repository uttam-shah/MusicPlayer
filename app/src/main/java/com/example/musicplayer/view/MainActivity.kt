package com.example.musicplayer.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.musicplayer.MainViewModel
import com.example.musicplayer.PlaySongActivity
import com.example.musicplayer.R
import com.example.musicplayer.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fetch and observe the music data
        viewModel.getMusics()

        // Observe the songTitles LiveData
        viewModel.songTitles.observe(this, Observer { songList ->
            // Initialize the ArrayAdapter when songList is updated
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, songList)
            binding.listview.adapter = adapter

            // Handle item click to play selected song
            binding.listview.setOnItemClickListener { _, _, position, _ ->
                val intent = Intent(this, PlaySongActivity::class.java)
                val currentSong = binding.listview.getItemAtPosition(position).toString()

//                val songs = viewModel.allSongs
//                playSong(songs[position])

                // Convert list to array and pass it via intent
                val songList = viewModel.allSongs.value?.map { it.absolutePath }?.let { ArrayList(it) }
                intent.putExtra("songList", songList)
                intent.putExtra("currentSong", currentSong)
                intent.putExtra("position", position)
                startActivity(intent)
            }
        })

        viewModel.allSongs.observe(this, Observer { song ->
            //playSong(song[0])
             //lateinit var mediaPlayer: MediaPlayer

//             val mediaPlayer = MediaPlayer()
//            mediaPlayer.setDataSource(song[0].path)
//            mediaPlayer.prepare()
//            mediaPlayer.start()
        })
    }

    private fun playSong(file: File) {
        // Release any existing MediaPlayer
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }

        // Initialize MediaPlayer with the selected song file
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.path) // Set file path of the song
            prepare()                // Prepare the player
            start()                  // Start playing
        }
    }
}

