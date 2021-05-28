package com.ditto.menuitems_ui.faq.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.databinding.FaqfragmentLayoutBinding
import com.ditto.menuitems_ui.faq.ui.adapters.FAQAdapter
import com.ditto.menuitems_ui.faq.ui.models.FAQModel
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import javax.inject.Inject

class FAQFragment(var fAQ: List<FAQModel>) : BaseFragment() {
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
                        val bundle =
                            bundleOf("videoPath" to path, "title" to "FAQ", "from" to "tutorial")

                        findNavController().navigate(
                            R.id.action_destination_faq_to_nav_graph_id_video,
                            bundle
                        )
                    }
                }, object : VisitSiteListener {
                    override fun onVisitClick(url: String) {
                        logger.d("URL== " + url,)
                    }
                })
        }
        binding.recyclerParent.adapter = faqadapter
        binding.recyclerParent.layoutManager = LinearLayoutManager(context)
    }
}