package com.ditto.videoplayer.utility

import android.util.Log
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player

class PlaybackStateListener : Player.EventListener {

    override fun onPlaybackStateChanged(playbackState: Int) {

        val stateString: String
        stateString = when (playbackState) {
            ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
            ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
            ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
            ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
            else -> "UNKNOWN_STATE             -"
        }
        Log.d("EXOPLAYER", "changed state to $stateString")
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        exoPlayerStateListener?.getPlayingState(isPlaying)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        exoPlayerStateListener?.onPlayerError(error?.cause?.message.toString())
    }

    interface ExoPlayerStateListener {
        fun getPlayingState(isPlaying: Boolean)
        fun onPlayerError(error: String?)
    }

    companion object {
        var exoPlayerStateListener: ExoPlayerStateListener? = null
    }
}