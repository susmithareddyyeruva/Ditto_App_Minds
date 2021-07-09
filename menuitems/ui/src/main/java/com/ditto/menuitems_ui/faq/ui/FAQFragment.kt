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
import com.ditto.menuitems.domain.model.faq.FAQDomain
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FaqfragmentLayoutBinding
import com.ditto.menuitems_ui.faq.ui.adapters.FAQAdapter
import com.ditto.videoplayer.CustomPlayerControlActivity
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import javax.inject.Inject


class FAQFragment(var fAQ: List<FAQDomain>) : BaseFragment() {
    @Inject
    lateinit var loggerFactory: LoggerFactory
    private val viewModel: FQAfragmentViewModel by ViewModelDelegate()
    lateinit var binding: FaqfragmentLayoutBinding

    val logger: Logger by lazy {
        loggerFactory.create(FAQFragment::class.java.simpleName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FaqfragmentLayoutBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.faqContainer
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val faqadapter = context?.let {
            FAQAdapter(
                it,
                fAQ, object : WatchVideoClickListener {
                    override fun onVideoClick(path: String) {
                        logger.d("path== " + path,)
                        if (findNavController().currentDestination?.id == R.id.nav_graph_mainfaq ||
                            findNavController().currentDestination?.id == R.id.destination_faq_glossary ){
                            val bundle =
                                bundleOf(
                                    "videoPath" to path,
                                    "title" to "FAQ",
                                    "from" to "tutorial"
                                )

                            val intent = Intent(requireContext(), CustomPlayerControlActivity::class.java)
                            intent.putExtras(bundle)
                            startActivity(intent)
                        }
                    }
                }, object : VisitSiteListener {
                    override fun onVisitClick(url: String) {
                        logger.d("URL== " + url,)
                        val viewIntent = Intent(
                            "android.intent.action.VIEW",
                            Uri.parse(url)
                        )
                        startActivity(viewIntent)
                    }
                })
        }
        binding.recyclerParent.adapter = faqadapter
        binding.recyclerParent.layoutManager = LinearLayoutManager(context)
    }
}