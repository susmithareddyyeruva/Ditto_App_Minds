package com.ditto.videoplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.*
import com.google.android.youtube.player.YouTubePlayerView
import core.appstate.AppState
import core.ui.common.Utility
import kotlinx.android.synthetic.main.activity_player.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class CustomPlayerControlActivity : YouTubeBaseActivity(),
    YouTubePlayer.OnInitializedListener, View.OnClickListener,Utility.CustomCallbackDialogListener {
    private var mPlayer: YouTubePlayer? = null
    private var mPlayButtonLayout: View? = null
    private var mPlayTimeTextView: TextView? = null
    private var skipButton: TextView? = null
    private var mHandler: Handler? = null
    private var mSeekBar: SeekBar? = null
    private var isPlay: Boolean = false
    private var videoUrl = ""
    private var tittle = ""
    private var from = ""
    override fun onBackPressed() {
        super.onBackPressed()
        if (AppState.getIsLogged()){
            finishAffinity()
        }else{
            val intent = Intent()
            intent.data = Uri.parse("ONBACK")
            setResult(Activity.RESULT_OK, intent)
            finish()
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        hideSystemUI()

        // Initializing YouTube player view
        val youTubePlayerView =
            findViewById<View>(R.id.video_view) as YouTubePlayerView
        youTubePlayerView.initialize(getString(R.string.youtube_api_key), this)
        //Add play button to explicitly play video in YouTubePlayerView
        mPlayButtonLayout = findViewById(R.id.video_control)
        findViewById<View>(R.id.play_video).setOnClickListener(this)
        findViewById<View>(R.id.close).setOnClickListener(this)
        findViewById<View>(R.id.skipButton).setOnClickListener(this)
        mPlayTimeTextView = findViewById<View>(R.id.play_time) as TextView
        skipButton = findViewById<View>(R.id.skipButton) as TextView
        mSeekBar = findViewById<View>(R.id.video_seekbar) as SeekBar
        mSeekBar!!.setOnSeekBarChangeListener(mVideoSeekBarChangeListener)
        mHandler = Handler()
        setupViews()
        VIDEO_ID=getYoutubeVideoId("https://youtu.be/KdjB0fIYyDM")?:""
        Log.d("VideoPlayer ID==", " $videoUrl")
    }

    private fun setupViews() {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            bundle?.getString("videoPath")?.let { videoUrl = it }
            bundle?.getString("title")?.let { title = it }
            bundle?.getString("from")?.let { from = it }
            Log.d("VideoPlayer", " title: $title")
            if (from == "tutorial") {
                findViewById<TextView>(R.id.skipButton).visibility = View.GONE
                findViewById<ImageView>(R.id.close).visibility = View.VISIBLE
                findViewById<TextView>(R.id.header).text = title
            } else {
                findViewById<TextView>(R.id.skipButton).visibility = View.VISIBLE
                findViewById<ImageView>(R.id.close).visibility = View.GONE
            }
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        result: YouTubeInitializationResult
    ) {
        Log.d("Youtube","Failed to initialize.")
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
        mPlayButtonLayout!!.visibility = View.VISIBLE

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
            isPlay=false
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
            Log.d("YOUTUBE ERROR","$arg0")
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
            val lengthPlayed = mPlayer!!.durationMillis * progress / 100.toLong()
            mPlayer!!.seekToMillis(lengthPlayed.toInt())
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
                finish()
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
        mPlayer!!.pause()
    }

    private fun playVideo() {
        play_video.setImageResource(R.drawable.exo_icon_pause)
        mPlayer!!.play()
    }

    private fun displayCurrentTime() {
        if (null == mPlayer) return
        val formattedTime =
            formatTime(mPlayer!!.durationMillis - mPlayer!!.currentTimeMillis)
        mPlayTimeTextView!!.text = formattedTime

        val duration = formatTime(mPlayer!!.durationMillis)
        val current = formatTime(mPlayer!!.currentTimeMillis)
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
        override fun run() {
            displayCurrentTime()
            mHandler!!.postDelayed(this, 100)
        }
    }

    companion object {
        //https://www.youtube.com/watch?v=<VIDEO_ID>
         var VIDEO_ID = "https://www.youtube.com/watch?v=IanggPhf7EY"
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
    private fun showAlert(message:String) {
        val errorMessage = message
        Utility.getCommonAlertDialogue(
           this,
            "",
            errorMessage,
            "",
            getString(R.string.str_ok),
            this,
            Utility.AlertType.NETWORK
            ,
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
}