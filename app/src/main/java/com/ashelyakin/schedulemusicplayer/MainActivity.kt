package com.ashelyakin.schedulemusicplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashelyakin.schedulemusicplayer.dialogFragment.InetUnavailableDialogFragment
import com.ashelyakin.schedulemusicplayer.dialogFragment.LoadFinishDialogFragment
import com.ashelyakin.schedulemusicplayer.dialogFragment.LoadStartDialogFragment
import com.ashelyakin.schedulemusicplayer.download.DownloadCallbacks
import com.ashelyakin.schedulemusicplayer.download.DownloadViewModel
import com.ashelyakin.schedulemusicplayer.download.Downloader
import com.ashelyakin.schedulemusicplayer.profile.Profile
import com.ashelyakin.schedulemusicplayer.profile.ProfileLoader
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.ashelyakin.schedulemusicplayer.recyclerView.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.atomic.AtomicInteger


class MainActivity : AppCompatActivity() {

    private lateinit var downloadViewModel: DownloadViewModel
    private lateinit var timezonePlaylistsViewModel: TimezonePlaylistsViewModel
    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profile = ProfileLoader.getProfileFromJson(this, "testWithoutComments.json")
        title = profile.name
        initPlaylistDataAndSetId(profile.schedule)

        downloadViewModel = ViewModelProvider(this).get(DownloadViewModel::class.java)

        val timezonePlaylistsViewModelFactory = TimezonePlaylistsViewModelFactory(profile.schedule)
        timezonePlaylistsViewModel = ViewModelProvider(this, timezonePlaylistsViewModelFactory)
                .get(TimezonePlaylistsViewModel::class.java)

        downloadMp3()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_playback ->{
                val i = Intent(this, PlaybackActivity::class.java)
                i.putExtra("schedule", profile.schedule)
                startActivity(i)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun btnProportionPlusClick(view: View){
        val proportionView = getProportionView(view)
        timezonePlaylistsViewModel.plusProportion(proportionView.tag as Int)
    }

    fun btnProportionMinusClick(view: View){
        val proportionView = getProportionView(view)
        timezonePlaylistsViewModel.minusProportion(proportionView.tag as Int)
    }

    var uuid = AtomicInteger(0)
    private fun initPlaylistDataAndSetId(schedule: Schedule) {
        for (day in schedule.days)
            for (timezone in day.timeZones)
                for (playlist in timezone.playlists){
                    val newID = uuid.getAndIncrement()
                    SchedulePlaylistsData.setPlaylistsData(newID, schedule.playlists.filter { it.id == playlist.playlistID }[0])
                    playlist.playlistID = newID
                }
    }

    private fun downloadMp3() {
        val downloader =
            Downloader(object :
                DownloadCallbacks {

                val loadStartDialogFragment = LoadStartDialogFragment()
                val inetUnavailableDialogFragment = InetUnavailableDialogFragment()
                val loadFinishDialogFragment = LoadFinishDialogFragment()

                override fun onLoadStart() {
                    loadStartDialogFragment.show(supportFragmentManager, "loadStartDialog")
                    Log.i("Downloader", "Download started")
                }

                override fun onProgressChanged(newProgress: Int) {
                    downloadViewModel.progress.postValue(newProgress)
                }

                override fun onLoadFinished() {
                    loadStartDialogFragment.dismiss()
                    loadFinishDialogFragment.show(supportFragmentManager, "loadFinishDialog")
                    Log.i("Downloader", "Download completed")
                    inflateRecycler()
                }


                override fun onLoadStopped() {
                    inetUnavailableDialogFragment.isCancelable = false
                    inetUnavailableDialogFragment.show(supportFragmentManager, "inetUnavailableDialogFragment")
                    Log.i("Downloader", "Download stopped")
                }

                override fun onLoadResume() {
                    inetUnavailableDialogFragment.dismiss()
                    Log.i("Downloader", "Download resumed")
                }

            })
        downloader.downloadMp3(this)
    }

    private fun inflateRecycler(){
        this.runOnUiThread {
            main_recyclerView.adapter = RecyclerAdapter(profile.schedule, timezonePlaylistsViewModel.timezonePlaylistsData, this)
            main_recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun getProportionView(view: View): TextView {
        val linearLayout = (view.parent as ViewGroup).parent as LinearLayout
        return linearLayout.findViewById(R.id.proportion)
    }
}