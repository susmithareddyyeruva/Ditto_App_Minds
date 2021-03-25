package com.ditto.home_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory
import com.example.home_ui.databinding.ShopFragmentBinding
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate
import javax.inject.Inject

class ShopFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(HomeFragment::class.java.simpleName)
    }
    private val viewModel: ShopViewModel by ViewModelDelegate()
    private lateinit var binding: ShopFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ShopFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        (activity as BottomNavigationActivity).setToolbarTitle("Shop Joann.com")
        return binding.shoproot
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}