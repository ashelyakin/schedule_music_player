package com.ashelyakin.schedulemusicplayer.activity

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashelyakin.schedulemusicplayer.R
import com.ashelyakin.schedulemusicplayer.timezonePlaylistsViewModel.TimezonePlaylistsViewModel
import com.ashelyakin.schedulemusicplayer.timezonePlaylistsViewModel.TimezonePlaylistsViewModelFactory
import com.ashelyakin.schedulemusicplayer.dialogFragment.InetUnavailableDialogFragment
import com.ashelyakin.schedulemusicplayer.dialogFragment.LoadFinishDialogFragment
import com.ashelyakin.schedulemusicplayer.dialogFragment.LoadStartDialogFragment
import com.ashelyakin.schedulemusicplayer.download.DownloadCallbacks
import com.ashelyakin.schedulemusicplayer.download.DownloadViewModel
import com.ashelyakin.schedulemusicplayer.download.Downloader
import com.ashelyakin.schedulemusicplayer.profile.Profile
import com.ashelyakin.schedulemusicplayer.profile.ProfileLoader
import com.ashelyakin.schedulemusicplayer.recyclerView.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var downloadViewModel: DownloadViewModel
    private lateinit var timezonePlaylistsViewModel: TimezonePlaylistsViewModel
    private lateinit var profile: Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profile = ProfileLoader.getProfileFromJson(this, "testWithoutComments.json")
        title = profile.name

        downloadViewModel = ViewModelProvider(this).get(DownloadViewModel::class.java)

        val timezonePlaylistsViewModelFactory = TimezonePlaylistsViewModelFactory(profile.schedule)
        timezonePlaylistsViewModel = ViewModelProvider(this, timezonePlaylistsViewModelFactory)
                .get(TimezonePlaylistsViewModel::class.java)

        observeDownloadState()
    }

    private fun observeDownloadState() {

        val loadStartDialogFragment = LoadStartDialogFragment()
        val inetUnavailableDialogFragment = InetUnavailableDialogFragment()
        val loadFinishDialogFragment = LoadFinishDialogFragment()

        downloadViewModel.downloadState.observe(this, Observer{
            when (it){
                DownloadViewModel.DownloadState.STARTED -> loadStartDialogFragment.show(supportFragmentManager, "loadStartDialog")
                DownloadViewModel.DownloadState.RESUMED -> inetUnavailableDialogFragment.dismiss()
                DownloadViewModel.DownloadState.STOPPED -> {
                    inetUnavailableDialogFragment.isCancelable = false
                    inetUnavailableDialogFragment.show(supportFragmentManager, "inetUnavailableDialogFragment")
                }
                DownloadViewModel.DownloadState.FINISHED -> {
                    loadStartDialogFragment.dismiss()
                    loadFinishDialogFragment.show(supportFragmentManager, "loadFinishDialog")
                    inflateRecycler()
                }
                else -> Log.i("Downloader", "Download completed")
            }
        })
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

    private fun getProportionView(view: View): TextView {
        val linearLayout = (view.parent as ViewGroup).parent as LinearLayout
        return linearLayout.findViewById(R.id.proportion)
    }

    private fun inflateRecycler(){
        main_recyclerView.adapter = RecyclerAdapter(profile.schedule, timezonePlaylistsViewModel.timezonePlaylistsData, this)
        main_recyclerView.layoutManager = LinearLayoutManager(this)
    }

}