package com.ashelyakin.schedulemusicplayer.profile

import android.content.Context
import android.util.Log
import java.io.IOException
import kotlin.system.exitProcess

class ProfileLoader {

    companion object {

        fun getProfileFromJson(context: Context, jsonFileName: String): Profile {
            return parseJson(context, jsonFileName)
        }

        private fun parseJson(context: Context, fileName: String): Profile {
            val json = getJsonDataFromAsset(context, fileName)
            if (json == null) {
                Log.e("ProfileLoader", "Error loading json")
                exitProcess(0)
            }
            val profile = Profile.fromJson(json)
            if (profile == null) {
                Log.e("ProfileLoader", "Error loading profile")
                exitProcess(0)
            }
            return profile
        }

        private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return null
            }
            return jsonString
        }
    }
}