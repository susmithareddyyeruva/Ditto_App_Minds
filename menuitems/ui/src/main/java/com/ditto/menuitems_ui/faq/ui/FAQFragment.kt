package com.ditto.menuitems_ui.faq.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.ditto.menuitems_ui.databinding.FaqfragmentLayoutBinding
import com.ditto.menuitems_ui.faq.ui.adapters.FAQAdapter
import com.ditto.menuitems_ui.faq.ui.json.JsonHelper
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import javax.inject.Inject

class FAQFragment() : BaseFragment() {
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
                context?.let { JsonHelper(it).getFAQData() }, object : WatchVideoClickListener {
                    override fun onVideoClick(path: String) {
                        logger.d("path== " + path,)
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