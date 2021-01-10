package com.ashelyakin.schedulemusicplayer.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ashelyakin.schedulemusicplayer.R
import com.ashelyakin.schedulemusicplayer.player.SchedulePlayer
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_playback.*

class PlaybackActivity: AppCompatActivity() {

    val TAG = "PlaybackActivity"

    private var isBtnPlayNow = true

    private lateinit var schedulePlayer: SchedulePlayer

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
        schedulePlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
        schedulePlayer.release()
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

    private fun initPlayer() {
        AndroidThreeTen.init(this)

        val changeViewTextCallbacks = object: ChangeViewTextCallbacks{

            override fun changePlaylistName(playlistName: String?) {
                if (playlistName == null){
                    playlist_name.text = "Нет запланированных плейлистов на текущее время"
                }
                else {
                    playlist_name.text = playlistName
                }
            }

            override fun changeTrackName(trackName: String?) {
                if (trackName == null){
                    track.text = "Нет текущих треков"
                }
                else {
                    track.text = trackName
                }
            }
        }

        val schedule: Schedule = intent.getParcelableExtra("schedule")!!
        schedulePlayer = SchedulePlayer(this, schedule, this.viewModelStore, changeViewTextCallbacks)
    }

    fun btnPlayClick(v: View) {
        Log.d(TAG, "btnPlayClick was pressed")

        schedulePlayer.changePlayerState(isBtnPlayNow)

        if (isBtnPlayNow) {
            btnPlay.setBackgroundResource(R.mipmap.stop)
        }
        else {
            btnPlay.setBackgroundResource(R.mipmap.play)
        }
        isBtnPlayNow = !isBtnPlayNow
    }

    fun btnNextTrackClick(v: View) {
        schedulePlayer.next()
    }

}