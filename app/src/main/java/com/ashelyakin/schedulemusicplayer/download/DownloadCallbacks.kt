package com.ashelyakin.schedulemusicplayer.download

interface DownloadCallbacks{

    fun onLoadStart()
    fun onProgressChanged(newProgress: Int)
    fun onLoadFinished()
    fun onLoadStopped()
    fun onLoadResume()
}