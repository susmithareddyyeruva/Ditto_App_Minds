package com.ditto.menuitems_ui.faq.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.menuitems_ui.databinding.FaqfragmentLayoutBinding
import com.ditto.menuitems_ui.faq.ui.adapters.FAQAdapter
import com.ditto.menuitems_ui.faq.ui.json.JsonHelper
import core.ui.BaseFragment
import core.ui.ViewModelDelegate

class FAQFragment() : BaseFragment() {

    private val viewModel: FQAfragmentViewModel by ViewModelDelegate()
    lateinit var binding: FaqfragmentLayoutBinding

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
                context?.let { JsonHelper(it).getFAQData() })
        }
        binding.recyclerParent.adapter = faqadapter
        binding.recyclerParent.layoutManager = LinearLayoutManager(context)
    }

}