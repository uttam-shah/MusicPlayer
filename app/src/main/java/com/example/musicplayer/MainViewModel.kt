package com.example.musicplayer

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val songTitles: MutableLiveData<List<String>> = MutableLiveData()
    val allSongs: MutableLiveData<List<File>> = MutableLiveData()

    private val repository = MainRepository(application)

    fun getMusics() {
        Dexter.withContext(getApplication())
            .withPermission(android.Manifest.permission.READ_MEDIA_AUDIO)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                    val mySongs = repository.fetchSongs(Environment.getExternalStorageDirectory())
                    val items = mySongs.map { it.name.replace(".mp3", "") }

                    allSongs.value = mySongs
                    songTitles.value = items
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // Handle permission denied
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            })
            .check()
    }
}
