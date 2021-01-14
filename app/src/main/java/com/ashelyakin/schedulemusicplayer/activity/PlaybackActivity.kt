package com.ashelyakin.schedulemusicplayer.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ashelyakin.schedulemusicplayer.R
import com.ashelyakin.schedulemusicplayer.player.ExoPlayerListener
import com.ashelyakin.schedulemusicplayer.player.PlayerViewModel
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.google.android.exoplayer2.MediaItem
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_playback.*

class PlaybackActivity: AppCompatActivity() {

    val TAG = "PlaybackActivity"

    private var isBtnPlayNow = true

    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
        Log.d(TAG, "initPlayer was completed")
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")
        if (!isBtnPlayNow) {
            findViewById<Button>(R.id.btnPlay).setBackgroundResource(R.mipmap.play)
        }
        isBtnPlayNow = !isBtnPlayNow
        playerViewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
        playerViewModel.release()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.playback_activity_menu,menu)
        title = ""
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    inner class PlayerCallbacks: com.ashelyakin.schedulemusicplayer.player.PlayerCallbacks{
        override fun play() {
            runOnUiThread {  playerViewModel.player.play()}
        }

        override fun pause() {
            runOnUiThread {  playerViewModel.player.pause() }
        }

        override fun next() {
            runOnUiThread {  playerViewModel.player.next() }
        }

        override fun release() {
            runOnUiThread {  playerViewModel.player.release() }
        }

        override fun addListener(listener: ExoPlayerListener) {
            runOnUiThread {  playerViewModel.player.addListener((listener)) }
        }

        override fun addMediaItems(mediaItems: List<MediaItem>) {
            runOnUiThread {  playerViewModel.player.addMediaItems(mediaItems) }
        }

        override fun prepare() {
            runOnUiThread {  playerViewModel.player.prepare() }
        }

    }

    private fun initPlayer() {
        AndroidThreeTen.init(this)

        val changeViewTextCallbacks = object: ChangeViewTextCallbacks{

            override fun changePlaylistName(playlistName: String?) {
                runOnUiThread{
                    if (playlistName == null){
                        playlist_name.text = "Нет запланированных плейлистов на текущее время"
                    }
                    else {
                        playlist_name.text = playlistName
                    }
                }
            }

            override fun changeTrackName(trackName: String?) {
                runOnUiThread{
                    if (trackName == null){
                        track.text = "Нет текущих треков"
                    }
                    else {
                        track.text = trackName
                    }
                }
            }
        }

        val schedule: Schedule = intent.getParcelableExtra("schedule")!!
        //val playerViewModelFactory = PlayerViewModelFactory(schedule, PlayerCallbacks(), this.viewModelStore, changeViewTextCallbacks, application)
        playerViewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)
        playerViewModel.initPlayer(schedule, PlayerCallbacks(), changeViewTextCallbacks)
    }

    fun btnPlayClick(v: View) {
        Log.d(TAG, "btnPlayClick was pressed")

        playerViewModel.changePlayerState(isBtnPlayNow)

        if (isBtnPlayNow) {
            btnPlay.setBackgroundResource(R.mipmap.stop)
        }
        else {
            btnPlay.setBackgroundResource(R.mipmap.play)
        }
        isBtnPlayNow = !isBtnPlayNow
    }

    fun btnNextTrackClick(v: View) {
        playerViewModel.next()
    }

}