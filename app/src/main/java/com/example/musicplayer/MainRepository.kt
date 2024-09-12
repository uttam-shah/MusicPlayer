package com.example.musicplayer

import android.content.Context
import com.karumi.dexter.Dexter
import java.io.File
import java.nio.file.Files

class MainRepository(context: Context) {

    fun fetchSongs(file: File): ArrayList<File> {
        val arrayList = ArrayList<File>()
        val songs = file.listFiles()

        if (songs != null) {
            for (myFile in songs) {
                if (!myFile.isHidden && myFile.isDirectory) {
                    arrayList.addAll(fetchSongs(myFile))
                } else {
                    if (myFile.name.endsWith(".mp3") && !myFile.name.startsWith(".")) {
                        arrayList.add(myFile)
                    }
                }
            }
        }
        return arrayList
    }

}