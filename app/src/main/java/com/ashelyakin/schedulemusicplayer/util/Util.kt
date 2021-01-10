package com.ashelyakin.schedulemusicplayer.util

import android.text.TextUtils
import android.util.Log
import com.ashelyakin.schedulemusicplayer.profile.SchedulePlaylist
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class Util {

    companion object {

        private val TAG = "MD5"

        fun isPlaylistIntegrity(schedulePlaylist: SchedulePlaylist?, filesDirPath: String): Boolean{
            if (schedulePlaylist == null)
                return true
            var res = true
            schedulePlaylist.files.map {
                val musicFile = File(filesDirPath, it.id.toString() + ".mp3")
                if (!checkMD5(it.md5File, musicFile)){
                    res = false
                    musicFile.delete()
                }
            }
            return res
        }

        private fun checkMD5(md5: String, updateFile: File?): Boolean {
            if (TextUtils.isEmpty(md5) || updateFile == null) {
                Log.e(TAG, "MD5 string empty or updateFile null")
                return false
            }
            val calculatedDigest = calculateMD5(updateFile)
            if (calculatedDigest == null) {
                Log.e(TAG, "calculatedDigest null")
                return false
            }
            Log.v(TAG, "Calculated digest: $calculatedDigest")
            Log.v(TAG, "Provided digest: $md5")
            return calculatedDigest.equals(md5, ignoreCase = true)
        }

        private fun calculateMD5(updateFile: File?): String? {
            val digest: MessageDigest
            digest = try {
                MessageDigest.getInstance("MD5")
            } catch (e: NoSuchAlgorithmException) {
                Log.e(TAG, "Exception while getting digest", e)
                return null
            }
            val inputStream: InputStream
            try {
                inputStream = FileInputStream(updateFile)
            } catch (e: FileNotFoundException) {
                Log.e(TAG, "Exception while getting FileInputStream", e)
                return null
            }
            val buffer = ByteArray(8192)
            var read: Int
            return try {
                while (inputStream.read(buffer).also { read = it } > 0) {
                    digest.update(buffer, 0, read)
                }
                val md5sum: ByteArray = digest.digest()
                val bigInt = BigInteger(1, md5sum)
                var output: String = bigInt.toString(16)
                // Fill to 32 chars
                output = String.format("%32s", output).replace(' ', '0')
                output
            } catch (e: IOException) {
                throw RuntimeException("Unable to process file for MD5", e)
            } finally {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Exception on closing MD5 input stream", e)
                }
            }
        }

    }
}