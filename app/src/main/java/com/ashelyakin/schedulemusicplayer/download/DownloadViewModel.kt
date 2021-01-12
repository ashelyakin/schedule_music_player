package com.ashelyakin.schedulemusicplayer.download

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ashelyakin.schedulemusicplayer.dialogFragment.InetUnavailableDialogFragment
import com.ashelyakin.schedulemusicplayer.dialogFragment.LoadFinishDialogFragment
import com.ashelyakin.schedulemusicplayer.dialogFragment.LoadStartDialogFragment

class DownloadViewModel(application: Application): AndroidViewModel(application) {

    var progress = MutableLiveData<Int>()

    enum class DownloadState{
        STARTED, RESUMED, STOPPED, FINISHED
    }

    var downloadState = MutableLiveData<DownloadState>()

    init {
        progress.value = 0

        val downloader =
                Downloader(object :
                        DownloadCallbacks {

                    override fun onLoadStart() {
                        downloadState.postValue(DownloadState.STARTED)
                        Log.i("Downloader", "Download started")
                    }

                    override fun onProgressChanged(newProgress: Int) {
                        progress.postValue(newProgress)
                    }

                    override fun onLoadResume() {
                        downloadState.postValue(DownloadState.RESUMED)
                        Log.i("Downloader", "Download resumed")
                    }

                    override fun onLoadStopped() {
                        downloadState.postValue(DownloadState.STOPPED)
                        Log.i("Downloader", "Download stopped")
                    }

                    override fun onLoadFinished() {
                        downloadState.postValue(DownloadState.FINISHED)
                        Log.i("Downloader", "Download completed")
                    }

                })

        downloader.downloadMp3(getApplication<Application>().applicationContext)
    }

}