package com.ashelyakin.schedulemusicplayer

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashelyakin.schedulemusicplayer.download.dialogFragment.InetUnavailableDialogFragment
import com.ashelyakin.schedulemusicplayer.download.dialogFragment.LoadFinishDialogFragment
import com.ashelyakin.schedulemusicplayer.download.dialogFragment.LoadStartDialogFragment
import com.ashelyakin.schedulemusicplayer.download.DownloadCallbacks
import com.ashelyakin.schedulemusicplayer.download.DownloadViewModel
import com.ashelyakin.schedulemusicplayer.download.Downloader
import com.ashelyakin.schedulemusicplayer.profile.Profile
import com.ashelyakin.schedulemusicplayer.profile.ProfileLoader
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.recyclerView.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: DownloadViewModel
    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(DownloadViewModel::class.java)

        profile = ProfileLoader.getProfileFromJson(this, "testWithoutComments.json")
        title = profile.name
        initPlaylistData(profile.schedule)
        downloadMp3(this)
    }

    private fun initPlaylistData(schedule: Schedule) {
        for(playlist in schedule.playlists)
            PlaylistsData.setPlaylistsData(playlist.id, playlist)
    }

    private fun downloadMp3(context: Context) {
        val downloader =
            Downloader(object :
                DownloadCallbacks {

                val loadStartDialogFragment = LoadStartDialogFragment()
                val inetUnavailableDialogFragment = InetUnavailableDialogFragment()
                val loadFinishDialogFragment = LoadFinishDialogFragment()

                override fun onLoadStart() {
                    loadStartDialogFragment.show(supportFragmentManager, "loadStartDialog")
                }

                override fun onProgressChanged(newProgress: Int) {
                    viewModel.progress.postValue(newProgress)
                }

                override fun onLoadFinished() {
                    loadStartDialogFragment.dismiss()
                    loadFinishDialogFragment.show(supportFragmentManager, "loadFinishDialog")
                    main_recyclerView.adapter = RecyclerAdapter(profile.schedule)
                    main_recyclerView.layoutManager = LinearLayoutManager(context)
                }


                override fun onLoadStopped() {
                    inetUnavailableDialogFragment.isCancelable = false
                    inetUnavailableDialogFragment.show(
                        supportFragmentManager,
                        "inetUnavailableDialogFragment"
                    )
                }

                override fun onLoadResume() {
                    inetUnavailableDialogFragment.dismiss()
                }

            })
        downloader.downloadMp3(this)
    }

}