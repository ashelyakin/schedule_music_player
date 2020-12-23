package com.ashelyakin.schedulemusicplayer.download

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DownloadViewModel: ViewModel() {

    var progress = MutableLiveData<Int>()

    init {
        progress.value = 0
    }

}