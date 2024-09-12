package com.example.musicplayer

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.databinding.ActivityPlaySongBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class PlaySongActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaySongBinding
    private var songs: ArrayList<File> = arrayListOf()
    private lateinit var mediaPlayer: MediaPlayer
    private var position: Int = 0
    private var isMediaPlayerInitialized = false

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initializing ViewBinding
        binding = ActivityPlaySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extract data from the intent
        val intent = intent


        val songPaths = intent.getStringArrayListExtra("songList") ?: arrayListOf()
         songs = songPaths.map { File(it) } as ArrayList<File>


        position = intent.getIntExtra("position", 0)
        val textContent = intent.getStringExtra("currentSong") ?: ""

        binding.textView.text = textContent
        binding.textView.isSelected = true


        if (songs.isNotEmpty() && position in songs.indices) {
            initializeMediaPlayer()
            setupSeekBar()
            setupButtons()
        } else {
        }

    }


    private fun initializeMediaPlayer() {
        val uri = Uri.parse(songs[position].toString())
        mediaPlayer = MediaPlayer.create(this, uri)
        mediaPlayer.start()
        binding.play.setImageResource(R.drawable.pause)
        isMediaPlayerInitialized = true
    }

    private fun setupSeekBar() {
        binding.seekBar.max = mediaPlayer.duration
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (::mediaPlayer.isInitialized) {
                    mediaPlayer.seekTo(seekBar.progress)
                }
            }
        })

        // Update seek bar with coroutine
        lifecycleScope.launch(Dispatchers.Main) {
            while (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                binding.seekBar.progress = mediaPlayer.currentPosition
                delay(800)
            }
        }
    }

    private fun setupButtons() {
        binding.play.setOnClickListener {
            if (::mediaPlayer.isInitialized) {
                if (mediaPlayer.isPlaying) {
                    binding.play.setImageResource(R.drawable.play)
                    mediaPlayer.pause()
                } else {
                    binding.play.setImageResource(R.drawable.pause)
                    mediaPlayer.start()
                }
            }
        }

        binding.previous.setOnClickListener {
            playPreviousSong()
        }

        binding.next.setOnClickListener {
            playNextSong()
        }
    }

    private fun playPreviousSong() {
        if (songs.isNotEmpty()) {
            releaseMediaPlayer()
            position = if (position != 0) position - 1 else songs.size - 1
            initializeMediaPlayer()
            updateUI()
        }
    }

    private fun playNextSong() {
        if (songs.isNotEmpty()) {
            releaseMediaPlayer()
            position = if (position != songs.size - 1) position + 1 else 0
            initializeMediaPlayer()
            updateUI()
        }
    }

    private fun releaseMediaPlayer() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        isMediaPlayerInitialized = false
    }

    private fun updateUI() {
        if (::mediaPlayer.isInitialized) {
            binding.seekBar.max = mediaPlayer.duration
            binding.textView.text = songs[position].name
        }
    }
}
