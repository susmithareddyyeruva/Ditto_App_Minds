package com.ditto.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.home_ui.R
import core.ui.BaseFragment
import core.ui.BottomNavigationActivity
import core.ui.ViewModelDelegate


class BuyPatternFragment : BaseFragment() {

    private val viewModel: BuyPatternViewModel by ViewModelDelegate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolbarViewModel.isShowTransparentActionBar.set(false)
        toolbarViewModel.isShowActionBar.set(true)
        (activity as BottomNavigationActivity).setToolbarTitle("Buy Patterns")
        (activity as BottomNavigationActivity).setToolbarIcon()
        (activity as BottomNavigationActivity).hidemenu()
        return inflater.inflate(R.layout.buy_pattern_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}
