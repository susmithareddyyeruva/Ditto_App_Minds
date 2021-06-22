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
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import javax.inject.Inject

class GlossaryFragment(var list: List<GlossaryDomain>) :BaseFragment(){
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
        val glossaryAdapter = context?.let {
            GlossaryAdapter(
                it,list,object :WatchVideoClickListener{
                    override fun onVideoClick(path: String) {
                        logger.d("path== " + path,)
                        if (findNavController().currentDestination?.id == R.id.nav_graph_mainFaq  ){
                            val bundle =
                                bundleOf(
                                    "videoPath" to path,
                                    "title" to "Glossary",
                                    "from" to "tutorial"
                                )

                            findNavController().navigate(
                                R.id.action_destination_faq_to_nav_graph_id_video,
                                bundle
                            )
                        }
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

}