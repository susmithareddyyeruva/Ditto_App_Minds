package com.ditto.videoplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.videoplayer.databinding.ActivityPlayerBinding
import com.ditto.videoplayer.utility.PlaybackStateListener
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT
import com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import javax.inject.Inject

class VideoPlayerFragment : BaseFragment(), PlaybackStateListener.ExoPlayerStateListener {
    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(VideoPlayerFragment::class.java.simpleName)
    }

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = false
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private val viewModel: VideoPlayerViewModel by ViewModelDelegate()
    private var playbackStateListener: PlaybackStateListener? = null
    lateinit var binding: ActivityPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityPlayerBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.videoRoot
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUIEvents()
        playbackStateListener = PlaybackStateListener()
        PlaybackStateListener.exoPlayerStateListener = this
    }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun handleEvent(event: VideoPlayerViewModel.Event) =
        when (event) {
            is VideoPlayerViewModel.Event.OnPlayButtonClicked -> {
                if (!core.network.Utility.isNetworkAvailable(requireContext())) {
                    showSnackBar(getString(R.string.no_internet_available))
                }else{
                    startPlayer()
                }
            }
            is VideoPlayerViewModel.Event.OnSkipButtonClicked -> {
                findNavController().navigate(R.id.action_VideoPlayer_to_Onboarding)
            }
            else -> logger.d("Invalid Event")
        }


    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (player == null) {
            initializePlayer()
        }
    }

    private fun initializePlayer() {
        if (player == null) {
            val trackSelector = DefaultTrackSelector(requireContext())
            trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            player = SimpleExoPlayer.Builder(requireContext())
                .setTrackSelector(trackSelector)
                .build()
        }
        binding.videoView.player = player
        /* val mediaItem =
             MediaItem.Builder()
                 .setUri(getString(R.string.media_url_mp4))
                 .setMimeType(MimeTypes.APPLICATION_MPD)
                 .build()*/
        // Replace this line
        val mediaItem =
            MediaItem.fromUri(getString(R.string.media_url_mp4))
        player?.setMediaItem(mediaItem)
        player?.setPlayWhenReady(playWhenReady)
        player?.seekTo(currentWindow, playbackPosition)
        player?.addListener(playbackStateListener!!)
        player?.prepare()
    }

    private fun startPlayer() {
        player!!.playWhenReady = true
        player!!.playbackState
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.removeListener(playbackStateListener!!)
            player!!.release()
            player = null
            PlaybackStateListener.exoPlayerStateListener = null
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.videoRoot.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onPause() {
//        if (Util.SDK_INT <= 23) {
        releasePlayer()
//        }
        super.onPause()
    }

    override fun getPlayingState(isPlaying: Boolean) {
        viewModel.isPlayButtonVisible.set(!isPlaying)
    }

    override fun onPlayerError(error: String?) {
        showSnackBar(error)
    }

    private fun showSnackBar(error: String?) {
        viewModel.isPlayButtonVisible.set(true)
        val errorMessage = getString(R.string.no_internet_available)
        //if (error.isNullOrEmpty()) getString(R.string.no_internet_available) else error
        Utility.showSnackBar(
            errorMessage,
            binding.videoView
        )
    }
}
