package com.ashelyakin.schedulemusicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ashelyakin.schedulemusicplayer.profile.ProfileLoader
import com.ashelyakin.schedulemusicplayer.profile.Schedule

class MainActivity : AppCompatActivity() {

    init {
        val profile = ProfileLoader.getProfileFromJson(this, "testWithoutComments.json")
        initPlaylistData(profile.schedule)
        downloadMp3()
    }

    private fun downloadMp3() {
        val downloader = Downloader(object :DownloadCallbacks{
            override fun onLoadStart() {
                TODO("Not yet implemented")
            }

            override fun onProgressChanged() {
                TODO("Not yet implemented")
            }

            override fun onLoadFinished() {
                TODO("Not yet implemented")
            }

            override fun onLoadStopped() {
                TODO("Not yet implemented")
            }

        })
        downloader.downloadMp3(this)
    }

    private fun initPlaylistData(schedule: Schedule) {
        for(playlist in schedule.playlists)
            PlaylistsData.setPlaylistsData(playlist.id, playlist)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}