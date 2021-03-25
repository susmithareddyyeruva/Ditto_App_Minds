package com.ditto.mylibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate

import trace.mylibrary.ui.databinding.FragmentPatternInstructionsBinding

class PatternInstructionsFragment : BaseFragment() {

    private val viewModel: PatternDescriptionViewModel by ViewModelDelegate()
    lateinit var binding: FragmentPatternInstructionsBinding

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPatternInstructionsBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomNavViewModel.visibility.set(false)
        (activity as BottomNavigationActivity).setToolbarTitle("Pattern Instructions")
        showPdfFromAssets(arguments?.getString("PatternName") + ".pdf")
    }

    private fun showPdfFromAssets(pdfName: String) {
        binding.pdfView.fromAsset(pdfName)
            .defaultPage(0) // set the default page to open
            .scrollHandle(DefaultScrollHandle(requireContext()))
            .onPageError { page, _ ->
                Toast.makeText(
                    requireContext(),
                    "Error loading pdf", Toast.LENGTH_LONG
                ).show()
            }
            .load()
    }

}