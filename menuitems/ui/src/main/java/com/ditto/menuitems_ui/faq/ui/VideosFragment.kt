package com.ditto.menuitems_ui.faq.ui


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems.domain.model.faq.VideosDomain
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.VideosFragmentBinding
import com.ditto.menuitems_ui.faq.ui.adapters.VideosAdapter
import com.ditto.videoplayer.CustomPlayerControlActivity
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import javax.inject.Inject

class VideosFragment :BaseFragment(){
    var list: List<VideosDomain> = emptyList()
    private val viewModel: VideosViewModel by ViewModelDelegate()
    lateinit var binding: VideosFragmentBinding
    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(FAQFragment::class.java.simpleName)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VideosFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.videoContainer
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(requireArguments()) {
            val videoList = getSerializable(VIDEO_LIST) as List<VideosDomain>
            list = if (videoList != null) {
                videoList
            } else {
                emptyList()
            }
        }

        val videosAdapter = context?.let {
            VideosAdapter(
                it,list,object :WatchVideoItemClickListener, Utility.CustomCallbackDialogListener {
                    override fun onVideoClick(tittle: String, path: String) {
                        logger.d("path== " + path,)
                        if (path.isNotEmpty()) {
                            if (findNavController().currentDestination?.id == R.id.nav_graph_mainfaq ||
                                findNavController().currentDestination?.id == R.id.destination_faq_glossary ){
                                val bundle =
                                    bundleOf(
                                        "videoPath" to path,
                                        "title" to tittle,
                                        "from" to "tutorial"
                                    )

                                val intent = Intent(requireContext(), CustomPlayerControlActivity::class.java)
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }
                        } else {
                            Utility.getCommonAlertDialogue(
                                requireContext(),
                                "",
                                getString(core.lib.R.string.no_video_available),
                                "",
                                getString(core.lib.R.string.str_ok),
                                this,
                                Utility.AlertType.NETWORK,
                                Utility.Iconype.FAILED
                            )
                        }

                    }

                    override fun onCustomPositiveButtonClicked(
                        iconype: Utility.Iconype,
                        alertType: Utility.AlertType,
                    ) {
                    }

                    override fun onCustomNegativeButtonClicked(
                        iconype: Utility.Iconype,
                        alertType: Utility.AlertType,
                    ) {
                    }

                },object :VisitSiteListener{
                    override fun onVisitClick(url: String) {
                        logger.d("URL== " + url,)
                        val viewIntent = Intent(
                            "android.intent.action.VIEW",
                            Uri.parse(url)
                        )
                        startActivity(viewIntent)
                    }

                }
            )
        }
        binding.recyclerVideos.adapter = videosAdapter
        binding.recyclerVideos.layoutManager = LinearLayoutManager(context)



    }

    companion object {
        private const val VIDEO_LIST = "VIDEO_LIST"

        fun newInstance(videoList: List<VideosDomain>) = VideosFragment().apply {
            arguments = bundleOf(
                VIDEO_LIST to videoList as java.io.Serializable,
            )
        }
    }
}