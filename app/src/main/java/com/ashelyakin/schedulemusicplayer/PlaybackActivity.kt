package com.ashelyakin.schedulemusicplayer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ashelyakin.schedulemusicplayer.player.ExoPlayerListener
import com.ashelyakin.schedulemusicplayer.player.SchedulePlayer
import com.ashelyakin.schedulemusicplayer.profile.Schedule
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_playback.*

class PlaybackActivity: AppCompatActivity() {

    lateinit var player: SimpleExoPlayer

    private var isBtnPlayNow = true

    private var btnPlayWasNotPressed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        if (!isBtnPlayNow) {
            findViewById<Button>(R.id.btnPlay).setBackgroundResource(R.mipmap.play)
        }
        isBtnPlayNow = !isBtnPlayNow
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
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

    fun btnPlayClick(v: View) {
        if (isBtnPlayNow) {
            btnPlay.setBackgroundResource(R.mipmap.stop)
            isBtnPlayNow = !isBtnPlayNow

            if (btnPlayWasNotPressed) {
                player.play()
                btnPlayWasNotPressed = false
            }
            else{
                player.next()
                player.play()
            }
        }
        else {
            btnPlay.setBackgroundResource(R.mipmap.play)
            isBtnPlayNow = !isBtnPlayNow

            player.pause()
        }
    }

    fun btnNextTrackClick(view: View) {
        player.next()
    }

    private fun initPlayer() {
        AndroidThreeTen.init(this)

        val schedule: Schedule = intent.getParcelableExtra("schedule")!!

        player = SimpleExoPlayer.Builder(this).build()
        player.repeatMode = Player.REPEAT_MODE_ALL
        player.addListener(ExoPlayerListener(this, player, schedule))

        val schedulePlayer = SchedulePlayer(this, schedule, player)
        schedulePlayer.start()
    }

}