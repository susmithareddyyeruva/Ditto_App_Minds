package com.ditto.videoplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.annotation.RequiresApi
import core.lib.BuildConfig
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.*
import com.google.android.youtube.player.YouTubePlayerView
import core.di.EncodeDecodeUtil
import core.network.NetworkUtility
import core.ui.common.Utility
import kotlinx.android.synthetic.main.custom_layout.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class CustomPlayerControlActivity : YouTubeBaseActivity(),
    YouTubePlayer.OnInitializedListener, View.OnClickListener,
    Utility.CustomCallbackDialogListener {
    private var mPlayer: YouTubePlayer? = null

    /*    private var mPlayButtonLayout: View? = null*/
    private var mPlayTimeTextView: TextView? = null
    private var skipButton: TextView? = null
    private var mHandler: Handler? = null
    private var mSeekBar: SeekBar? = null
    private var isPlay: Boolean = false
    private var videoUrl = ""
    private var tittle = ""
    private var from = ""
    lateinit var youTubePlayerView:YouTubePlayerView

    override fun onBackPressed() {
        super.onBackPressed()
        if (from == "LOGIN") {
            finishAffinity()
        } else {
            val intent = Intent()
            intent.data = Uri.parse("ONBACK")
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!NetworkUtility.isNetworkAvailable(this)) {
            showAlert()
        }
    }

    override fun onPause() {
        pauseVideo()
        isPlay = true
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_layout)
        fullScreenCall()
        hideSystemUI()

        // Initializing YouTube player view
         youTubePlayerView =
            findViewById<View>(R.id.video_view) as YouTubePlayerView
        youTubePlayerView.initialize(EncodeDecodeUtil.decodeBase64(BuildConfig.YOUTUBE_API_KEY_BASE64), this)
        //Add play button to explicitly play video in YouTubePlayerView
        /*    mPlayButtonLayout = findViewById(R.id.video_control)*/
        findViewById<View>(R.id.play_video).setOnClickListener(this)
        findViewById<View>(R.id.close).setOnClickListener(this)
        findViewById<View>(R.id.skipButton).setOnClickListener(this)
        mPlayTimeTextView = findViewById<View>(R.id.play_time) as TextView
        skipButton = findViewById<View>(R.id.skipButton) as TextView
        mSeekBar = findViewById<View>(R.id.video_seekbar) as SeekBar
        mSeekBar!!.setOnSeekBarChangeListener(mVideoSeekBarChangeListener)
        mHandler = Handler()
        setupViews()
    }

    private fun setupViews() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            bundle?.getString("videoPath")?.let { videoUrl = it }
            bundle?.getString("title")?.let { title = it }
            bundle?.getString("from")?.let { from = it }
            VIDEO_ID = getYoutubeVideoId(videoUrl) ?: ""
            if (from == "tutorial") {
                findViewById<TextView>(R.id.skipButton).visibility = View.GONE
                findViewById<ImageView>(R.id.close).visibility = View.VISIBLE
                findViewById<TextView>(R.id.header).text = title
            } else {
                findViewById<TextView>(R.id.skipButton).visibility = View.VISIBLE
                findViewById<ImageView>(R.id.close).visibility = View.GONE

                //VIDEO_ID=getYoutubeVideoId("https://youtu.be/IanggPhf7EY")?:""  //For Testing purpose private video URL

            }
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        result: YouTubeInitializationResult
    ) {

    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider,
        player: YouTubePlayer,
        wasRestored: Boolean
    ) {
        if (null == player) return
        mPlayer = player
        displayCurrentTime()

        // Start buffering
        if (!wasRestored) {
            player.loadVideo(VIDEO_ID)
        }
        player.setPlayerStyle(PlayerStyle.CHROMELESS)
        /*  mPlayButtonLayout!!.visibility = View.VISIBLE*/

        // Add listeners to YouTubePlayer instance
        player.setPlayerStateChangeListener(mPlayerStateChangeListener)
        player.setPlaybackEventListener(mPlaybackEventListener)
    }

    private fun hideSystemUI() {
        val decorView: View = this.window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {

            val uiOptions = decorView.systemUiVisibility
            var newUiOptions = uiOptions
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_LOW_PROFILE
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_FULLSCREEN
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE
            newUiOptions = newUiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = newUiOptions
        }
    }

    private var mPlaybackEventListener: PlaybackEventListener = object : PlaybackEventListener {
        override fun onBuffering(arg0: Boolean) {}
        override fun onPaused() {
            mHandler!!.removeCallbacks(runnable)
        }

        override fun onPlaying() {
            play_video.setImageResource(R.drawable.exo_icon_pause)
            isPlay = false
            mHandler!!.postDelayed(runnable, 100)
            displayCurrentTime()
        }

        override fun onSeekTo(arg0: Int) {
            mHandler!!.postDelayed(runnable, 100)
        }

        override fun onStopped() {
            mHandler!!.removeCallbacks(runnable)
        }
    }
    var mPlayerStateChangeListener: PlayerStateChangeListener = object : PlayerStateChangeListener {
        override fun onAdStarted() {}
        override fun onError(arg0: YouTubePlayer.ErrorReason) {
            if (arg0 == YouTubePlayer.ErrorReason.NETWORK_ERROR) {
                showAlert("No Internet Connection available !")
            }
        }

        override fun onLoaded(arg0: String) {}
        override fun onLoading() {}
        override fun onVideoEnded() {}
        override fun onVideoStarted() {
            displayCurrentTime()
        }
    }
    var mVideoSeekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(
            seekBar: SeekBar,
            progress: Int,
            fromUser: Boolean
        ) {
            if (fromUser) {
                if (mPlayer != null) {
                    val lengthPlayed = mPlayer!!.durationMillis * progress / 100.toLong()
                    mPlayer!!.seekToMillis(lengthPlayed.toInt())
                }
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.play_video -> {
                if (isPlay) {
                    playVideo()
                    isPlay = false
                } else {
                    pauseVideo()
                    isPlay = true
                }
            }
            R.id.close -> {
                onBackPressed()
            }
            R.id.skipButton -> {
                val intent = Intent()
                intent.data = Uri.parse("SUCCESS")
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

    }

    private fun pauseVideo() {
        play_video.setImageResource(R.drawable.exo_icon_play)
        if (mPlayer != null) {
            try {
                mPlayer?.pause()
            } catch (e: IllegalStateException) {
                //mPlayer// check how to initialize
                if(!::youTubePlayerView.isInitialized) {
                    youTubePlayerView.initialize(EncodeDecodeUtil.decodeBase64(BuildConfig.YOUTUBE_API_KEY_BASE64), this)
                }
            }
        }
    }

    private fun playVideo() {
        play_video.setImageResource(R.drawable.exo_icon_pause)
        if (mPlayer != null) {
            try {
                mPlayer?.play()
            } catch (e: IllegalStateException) {
                //mPlayer// check how to initialize
                if(!::youTubePlayerView.isInitialized) {
                    youTubePlayerView.initialize(EncodeDecodeUtil.decodeBase64(BuildConfig.YOUTUBE_API_KEY_BASE64), this)
                }
            }
        }
    }

    private fun displayCurrentTime() {
        if (null == mPlayer) return
        val formattedTime =
            formatTime(mPlayer!!.durationMillis - mPlayer!!.currentTimeMillis)
        mPlayTimeTextView!!.text = formattedTime
    }

    private fun formatTime(millis: Int): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return (if (hours == 0) "--:" else "$hours:") + String.format(
            "%02d:%02d",
            minutes % 60,
            seconds % 60
        )
    }

    private val runnable: Runnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun run() {
            displayCurrentTime()
            if (mPlayer != null) {
                val playPercent =
                    (mPlayer!!.currentTimeMillis.toFloat() / mPlayer!!.durationMillis
                        .toFloat() * 100).toInt()
                println("get youtube displayTime 2 : $playPercent")
                // update live progress
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M){
                    mSeekBar!!.setProgress(playPercent, true)
                } else{
                    mSeekBar!!.progress = playPercent
                }
                mHandler!!.postDelayed(this, 100)
            }

        }
    }

    companion object {
        //https://www.youtube.com/watch?v=<VIDEO_ID>
        var VIDEO_ID = ""
    }

    private fun getYoutubeVideoId(youtubeUrl: String?): String? {
        var videoId: String? = ""
        if (youtubeUrl != null && youtubeUrl.trim { it <= ' ' }.length > 0 && youtubeUrl.startsWith(
                "http"
            )
        ) {
            val expression =
                "^.*((youtu.be" + "/)" + "|(v/)|(/u/w/)|(embed/)|(watch\\?))\\??v?=?([^#&\\?]*).*" // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher: Matcher = pattern.matcher(youtubeUrl)
            if (matcher.matches()) {
                val groupIndex1: String = matcher.group(7)
                if (groupIndex1 != null && groupIndex1.length == 11) videoId = groupIndex1
            }
        }
        return videoId
    }

    private fun showAlert(message: String) {
        val errorMessage = message
        Utility.getCommonAlertDialogue(
            this,
            "",
            errorMessage,
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.NETWORK,
            Utility.Iconype.FAILED
        )
    }

    override fun onCustomPositiveButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

    }

    override fun onCustomNegativeButtonClicked(
        iconype: Utility.Iconype,
        alertType: Utility.AlertType
    ) {

    }

    private fun showAlert() {
        val errorMessage = getString(R.string.no_internet_available)
        Utility.getCommonAlertDialogue(
            this,
            "",
            errorMessage,
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.NETWORK,
            Utility.Iconype.FAILED
        )
    }

    override fun onDestroy() {
        if (mPlayer != null) {
            mPlayer?.release()
            mPlayer = null
        }
        super.onDestroy()
    }

    private fun fullScreenCall() {
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        val behavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        val type = WindowInsetsCompat.Type.systemBars()
        insetsController.systemBarsBehavior = behavior
        insetsController.hide(type)
    }
}