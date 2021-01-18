package com.ashelyakin.schedulemusicplayer.download

import android.content.Context
import com.ashelyakin.schedulemusicplayer.SchedulePlaylistsData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Downloader(private val callbacks: DownloadCallbacks) {

    fun downloadMp3(context: Context) {
        GlobalScope.launch {
            callbacks.onLoadStart()

            val fileSet = HashSet<com.ashelyakin.schedulemusicplayer.profile.File>()
            SchedulePlaylistsData.getPlaylistsData().forEach{ fileSet.addAll(it.value.files) }

            var progress = 0
            val step = 100/fileSet.size

            for (musicFile in fileSet) {
                val musicFileAbsolutePath = context.filesDir.absolutePath + "/" + musicFile.id + ".mp3"
                if (!File(musicFileAbsolutePath).exists()) {
                    ifInetIsUnavailable()
                    getMp3FromURL(musicFile.fileName, musicFileAbsolutePath)
                }

                progress += step
                callbacks.onProgressChanged(progress)
            }
            callbacks.onLoadFinished()
        }
    }

    private fun ifInetIsUnavailable() {
        var isResumed = false
        while (!isInternetAvailable()) {
            if (!isResumed) {
                callbacks.onLoadStopped()
                isResumed = true
            }
            Thread.sleep(2000)
        }
        if (isResumed)
            callbacks.onLoadResume()
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val p1 = Runtime.getRuntime().exec("ping -c 1 www.google.com")
            return p1.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    private fun getMp3FromURL(url_path: String, fileAbsolutePath: String) {
        val fos = FileOutputStream(File(fileAbsolutePath))

        val url = URL(url_path)
        val httpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "GET"
        httpURLConnection.doOutput = false

        val inputStream = httpURLConnection.inputStream
        try {

            httpURLConnection.connect()

            var downloadSize = 0
            val buffer = ByteArray(1024)
            var bufferLength: Int
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fos.write(buffer, 0, bufferLength)
                downloadSize += bufferLength
            }

        } catch (e: Exception) {
        }
        finally {
            fos.close()
            inputStream.close()
        }
    }

}