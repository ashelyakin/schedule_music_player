package com.ashelyakin.schedulemusicplayer.activity

import android.app.ActivityManager
import android.app.ActivityManager.RecentTaskInfo
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.ashelyakin.schedulemusicplayer.BackgroundStart
import com.ashelyakin.schedulemusicplayer.ForegroundCallbacks
import com.ashelyakin.schedulemusicplayer.PlayerApplication
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

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        context = this
        this.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager

       /* val startForegroundedActivityIntent = Intent(context, MainActivity::class.java)
        startForegroundedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//optional
        startForegroundedActivityIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//will cause it to come to foreground
        startForegroundedActivityIntent.putExtra("ID_TimeLeft","test");
        startActivity(startForegroundedActivityIntent)*/

        //registerReceiver(Receiver(), IntentFilter("com.ashelyakin.schedulemusicplayer.START_FOREGROUND_ACTIVITY"))
        PlayerApplication.setForegroundedActivity(object: ForegroundCallbacks{
            override fun start() {
                runOnUiThread {
                    /*val fullScreenIntent = Intent(context, PlaybackActivity::class.java)
                    val fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                    val notificationBuilder = NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("Incoming call")
                            .setContentText("(919) 555-1234") //The following are the key 3 lines
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setFullScreenIntent(fullScreenPendingIntent, true)

                    val notifyManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notifyManager.notify(7777, notificationBuilder.build())*/

                    val startForegroundedActivityIntent = Intent(context, PlaybackActivity::class.java)
                    val showOperation = PendingIntent.getActivity(context, 0, startForegroundedActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(System.currentTimeMillis() + 1000, showOperation)
                    val am = context!!.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
                    am.setAlarmClock(alarmClockInfo, showOperation)

                }
            }
        })
        //initPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                //Request permission if not authorized
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, 0)
            }
        }
        if (!BackgroundStart.isAccessGranted(this)) {
            Log.e(TAG, "!isAccessGranted")
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }
        if (!BackgroundStart.canBackgroundStart(context)) {
            Log.e(TAG, "!canBackgroundStart")
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        Log.d(TAG, "initPlayer was completed")
    }

    private fun launchApp(packageName: String) {
        val runningAppInfo = getRecentTaskInfo(packageName)

        // If already running, just bring it to foreground
        if (runningAppInfo != null) {
            startActivity(runningAppInfo.baseIntent)

            // Otherwise, start new task
        } else {
            startActivity(packageManager.getLaunchIntentForPackage(packageName))
        }
    }

    private fun getRecentTaskInfo(packageName: String): RecentTaskInfo? {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        try {
            // 10 : # of tasks you want to take a look at
            val infoList = manager.getRecentTasks(10, 0)
            for (info in infoList) {
                if (info.baseIntent.component!!.packageName == packageName) {
                    return info
                }
            }
        } catch (e: NullPointerException) {
        }
        return null
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart()")
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
        //playerViewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
        //playerViewModel.release()
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
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
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