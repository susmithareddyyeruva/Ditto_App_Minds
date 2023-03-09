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
import com.ditto.menuitems.domain.model.faq.GlossaryDomain
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.GlossaryFragmentBinding
import com.ditto.menuitems_ui.faq.ui.adapters.GlossaryAdapter
import com.ditto.videoplayer.CustomPlayerControlActivity
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import core.ui.common.Utility
import javax.inject.Inject
import javax.microedition.khronos.opengles.GL

class GlossaryFragment() :BaseFragment(){
    var list: List<GlossaryDomain> = emptyList()
    private val viewModel: GlossaryViewModel by ViewModelDelegate()
    lateinit var binding: GlossaryFragmentBinding
    @Inject
    lateinit var loggerFactory: LoggerFactory
    val logger: Logger by lazy {
        loggerFactory.create(FAQFragment::class.java.simpleName)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GlossaryFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.glossaryContainer
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with (requireArguments()) {
            val glossaryList = getSerializable(GLOSSARY_LIST) as List<GlossaryDomain>
            list = if (glossaryList != null) {
                glossaryList
            } else {
                emptyList()
            }
        }

        val glossaryAdapter = context?.let {
            GlossaryAdapter(
                it,list,object :WatchVideoClickListener, Utility.CustomCallbackDialogListener {
                    override fun onVideoClick(path: String) {
                        logger.d("path== " + path,)
                        if (path.isNotEmpty()) {
                            if (findNavController().currentDestination?.id == R.id.nav_graph_mainfaq ||
                                findNavController().currentDestination?.id == R.id.destination_faq_glossary ){
                                val bundle =
                                    bundleOf(
                                        "videoPath" to path,
                                        "title" to "Glossary",
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
        binding.recyclerGlossary.adapter = glossaryAdapter
        binding.recyclerGlossary.layoutManager = LinearLayoutManager(context)



    }

    companion object {
        private const val GLOSSARY_LIST = "GLOSSARY_LIST"

        fun newInstance(glossaryDomains: List<GlossaryDomain>) = GlossaryFragment().apply {
            arguments = bundleOf(
                GLOSSARY_LIST to glossaryDomains as java.io.Serializable,
            )
        }
    }

}