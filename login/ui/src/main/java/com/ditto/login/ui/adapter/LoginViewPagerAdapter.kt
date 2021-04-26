package com.ditto.login.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.ditto.login.domain.model.LoginViewPagerData
import com.ditto.login.ui.LoginViewModel
import com.ditto.login.ui.R
import com.ditto.login.ui.databinding.LoginImageAdapterBinding
import core.binding.BindableAdapter

class LoginViewPagerAdapter : PagerAdapter(), BindableAdapter<List<LoginViewPagerData>> {

    lateinit var viewModel: LoginViewModel
    private var imageData: List<LoginViewPagerData> = emptyList()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun getCount(): Int {
        return imageData.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val binding = LoginImageAdapterBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.ivViewpagerLogin.setImageDrawable(container.context.getDrawable(R.drawable.mask_group5))
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun setListData(listData: List<LoginViewPagerData>) {
        imageData = listData
        notifyDataSetChanged()
    }
}