package com.ditto.login.ui.adapter

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.ditto.login.domain.model.LoginViewPagerData
import com.ditto.login.ui.LoginViewModel
import com.ditto.login.ui.R
import com.ditto.login.ui.databinding.LoginImageAdapterBinding
import core.binding.BindableAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException

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
        /*GlobalScope.launch {
            try {
                var theBitmap = Glide
                    .with(container.context)
                    .load(imageData[position].imageUrl)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_launcher_background)
                    .imageDecoder(SvgBitmapDecoder(container.context))
                    .into(SIZE_ORIGINAL, SIZE_ORIGINAL)
                    .get()

                withContext(Dispatchers.Main) {
                    binding.ivViewpagerLogin.setImageBitmap(theBitmap)
                }

            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }*/
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