package com.ashelyakin.schedulemusicplayer

interface DownloadCallbacks{

    fun onLoadStart()
    fun onProgressChanged()
    fun onLoadFinished()
    fun onLoadStopped()
}