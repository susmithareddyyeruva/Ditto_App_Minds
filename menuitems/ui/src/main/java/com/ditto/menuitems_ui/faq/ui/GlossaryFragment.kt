package com.ditto.menuitems_ui.faq.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ditto.menuitems_ui.databinding.GlossaryFragmentBinding
import com.ditto.menuitems_ui.faq.ui.adapters.GlossaryAdapter
import com.ditto.menuitems_ui.faq.ui.models.FAQModel
import core.ui.BaseFragment
import core.ui.ViewModelDelegate

class GlossaryFragment(var list: List<FAQModel>) :BaseFragment(){
    private val viewModel: GlossaryViewModel by ViewModelDelegate()
    lateinit var binding: GlossaryFragmentBinding

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
                it,list
                )
        }
        binding.recyclerGlossary.adapter = glossaryAdapter
        binding.recyclerGlossary.layoutManager = LinearLayoutManager(context)



    }

}