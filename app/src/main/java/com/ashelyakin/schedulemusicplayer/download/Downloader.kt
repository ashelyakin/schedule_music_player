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

            val fileList = SchedulePlaylistsData.getPlaylistsData()
                .flatMap { it.files }
            var progress = 0
            val step = 100/fileList.size

            for (musicFile in fileList) {
                ifInetIsUnavailable()

                val musicFileAbsolutePath = context.filesDir.absolutePath + "/" + musicFile.id + ".mp3"
                getMp3FromURL(musicFile.fileName, musicFileAbsolutePath)

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
        try {
            val url = URL(url_path)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.doOutput = false
            httpURLConnection.connect()

            val file = File(fileAbsolutePath)
            val fos = FileOutputStream(file)
            val inputStream = httpURLConnection.inputStream

            var downloadSize = 0
            val buffer = ByteArray(1024)
            var bufferLength: Int
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fos.write(buffer, 0, bufferLength)
                downloadSize += bufferLength
            }
            fos.close()
            inputStream.close()
        } catch (e: Exception) {
        }
    }

}