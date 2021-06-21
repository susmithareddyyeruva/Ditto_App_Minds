package com.ditto.videoplayer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.*
import com.google.android.youtube.player.YouTubePlayerView
import kotlinx.android.synthetic.main.activity_player.*

class CustomPlayerControlActivity : YouTubeBaseActivity(),
    YouTubePlayer.OnInitializedListener, View.OnClickListener {
    private var mPlayer: YouTubePlayer? = null
    private var mPlayButtonLayout: View? = null
    private var mPlayTimeTextView: TextView? = null
    private var mHandler: Handler? = null
    private var mSeekBar: SeekBar? = null
    private var isPlay: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
        setContentView(R.layout.activity_player)

        // Initializing YouTube player view
        val youTubePlayerView =
            findViewById<View>(R.id.video_view) as YouTubePlayerView
        youTubePlayerView.initialize(API_KEY, this)


        //Add play button to explicitly play video in YouTubePlayerView
        mPlayButtonLayout = findViewById(R.id.video_control)
        findViewById<View>(R.id.play_video).setOnClickListener(this)
        findViewById<View>(R.id.close).setOnClickListener(this)
        mPlayTimeTextView = findViewById<View>(R.id.play_time) as TextView
        mSeekBar = findViewById<View>(R.id.video_seekbar) as SeekBar
        mSeekBar!!.setOnSeekBarChangeListener(mVideoSeekBarChangeListener)
        mHandler = Handler()
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider,
        result: YouTubeInitializationResult
    ) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show()
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
            player.cueVideo(VIDEO_ID)
        }
        player.setPlayerStyle(PlayerStyle.CHROMELESS)
        isPlay = true
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
        override fun onError(arg0: YouTubePlayer.ErrorReason) {}
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
        val id = v.id
        if (id == R.id.play_video) {
            if (isPlay) {
                playVideo()
                isPlay = false
            } else {
                pauseVideo()
                isPlay = true
            }
        } else if (id == R.id.close) {
            val intent = Intent()
            intent.data = Uri.parse("SUCCESS")
            setResult(Activity.RESULT_OK, intent)
            finish()
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
        private val TAG = CustomPlayerControlActivity::class.java.simpleName
        const val API_KEY = "AIzaSyCkPkU1MbL78LtkMmjYrkdMatURfwrlOVE"

        //https://www.youtube.com/watch?v=<VIDEO_ID>
        const val VIDEO_ID = "Jli6Sqm-2DU"
    }
}