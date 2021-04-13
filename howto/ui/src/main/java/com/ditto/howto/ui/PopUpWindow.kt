package com.ditto.howto.ui

/**
 * Created by Vishnu A V on  03/08/2020.
 * Popup Class wich plays the video
 */
import android.app.ActionBar
import android.app.Activity
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.ditto.howto.utils.Common
import com.ditto.howto.utils.MyMediaController
import com.ditto.howto_ui.R
import kotlinx.android.synthetic.main.popup_window.*

class PopUpWindow : AppCompatActivity() {

    var sPlayPosition: Int = 0
    private var mediaController: MediaController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.popup_window)
        setUI()
        val videopath: String? = intent.getStringExtra("filename")
        if (videopath != null) {
            playVideo(videopath)
        }
        close.setOnClickListener {
            finish()
            overridePendingTransition(0, 0);
        }
        /*window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
            val actionBar: ActionBar? = actionBar
            if (actionBar != null) {
                actionBar.hide()
            }
        }
    }

    private fun playVideo(filename: String) {
        val videoUri: Uri = getMedia(filename)
        video.setVideoURI(videoUri)
        mediaController = MyMediaController(this)
        mediaController?.setAnchorView(video)
        mediaController?.setMediaPlayer(video)
        video.setMediaController(mediaController)
        video.start()
        video.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mediaPlayer: MediaPlayer?) {
                setfullscreen()
                val parentLayout: RelativeLayout = video.getParent() as RelativeLayout
                val parentParams: ConstraintLayout.LayoutParams =
                    parentLayout.getLayoutParams() as ConstraintLayout.LayoutParams
                /*parentParams.height = mediaPlayer?.videoHeight!!
                parentParams.width = mediaPlayer?.videoWidth!!*/
                parentLayout.setLayoutParams(parentParams)

                val frameLayout: FrameLayout = mediaController!!.parent as FrameLayout
                val layoutParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ) as RelativeLayout.LayoutParams
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

                (frameLayout.getParent() as ViewGroup).removeView(frameLayout)
                parentLayout.addView(frameLayout, layoutParams)

                mediaController!!.setAnchorView(video)
                mediaController!!.hide()
                (mediaController as MyMediaController).hide()

                video.setMediaController(mediaController)
                mediaController?.setMediaPlayer(video)

            }

        })
    }

    private fun setUI() {
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(this, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            this.window.statusBarColor = Color.TRANSPARENT
            setWindowFlag(this, false)
        }

        popup_window_background.setBackgroundColor(Color.TRANSPARENT)
        popup_window_view_with_border.alpha = 1f
    }

    fun setfullscreen() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val params =
            video.getLayoutParams()
        params.width = metrics.widthPixels
        params.height = metrics.heightPixels
        video.setLayoutParams(params)
    }

    /**
     * onPause
     */
    override fun onPause() {
        super.onPause()
        sPlayPosition = video.currentPosition
        video.pause()
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()
        video.seekTo(sPlayPosition)
        video.resume()
    }

    /**
     * onDestroy
     */
    override fun onDestroy() {
        super.onDestroy()
        sPlayPosition = 0
        Common.isShowingVideoPopup.set(false)
    }

    /**
     * function for getting video URI from RAW file
     */
    fun getMedia(mediaName: String): Uri {
        return Uri.parse(
            "android.resource://" + packageName +
                    "/raw/" + mediaName
        )
    }

    /**
     * function for getting attributes of current window
     */
    private fun setWindowFlag(activity: Activity, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        } else {
            winParams.flags =
                winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        }
        win.attributes = winParams
    }

    /**
     * onBackPressed. Closing popup with animation
     */
    override fun onBackPressed() {
        video.stopPlayback()
        finish()

    }

}