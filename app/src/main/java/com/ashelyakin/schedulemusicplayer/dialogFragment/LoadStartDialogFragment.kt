package com.ashelyakin.schedulemusicplayer.dialogFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ashelyakin.schedulemusicplayer.download.DownloadViewModel
import com.ashelyakin.schedulemusicplayer.R
import kotlinx.android.synthetic.main.load_start_dialog.*

class LoadStartDialogFragment: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setTitle("Загрузка mp3 файлов")
        val viewModel = ViewModelProvider(activity ?: this).get(DownloadViewModel::class.java)
        viewModel.progress.observe(viewLifecycleOwner, ProgressObserver())

        return inflater.inflate(R.layout.load_start_dialog, container, false)
    }

    inner class ProgressObserver: Observer<Int>{

        override fun onChanged(newProgress: Int?) {
            progress_bar.progress = newProgress!!
            if (newProgress == 0) {
                progress_bar.secondaryProgress = 0
            } else {
                progress_bar.secondaryProgress = newProgress + 5
            }
            progress_text.text = "$newProgress%"
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
        isCancelable = false
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = RelativeLayout.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }
}