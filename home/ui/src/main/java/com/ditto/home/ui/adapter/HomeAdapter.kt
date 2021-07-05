package com.ditto.home.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ditto.home.domain.model.HomeData
import com.ditto.home.ui.HomeViewModel
import com.example.home_ui.R
import com.example.home_ui.databinding.HomeItemBinding
import core.binding.BindableAdapter


/**
 * Adapter class to map List<HomeData> with RecyclerView
 */
class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeHolder>(),
    BindableAdapter<List<HomeData>> {

    lateinit var viewModel: HomeViewModel
    private var homeData: List<HomeData> = emptyList()
    val images = intArrayOf(
        R.drawable.ic_home_tutorial,  R.drawable.ic_home_pattern_library, R.drawable.ic_home_ditto,
        R.drawable.ic_home_joann)

    override fun setListData(items: List<HomeData>) {
        homeData = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomeItemBinding.inflate(inflater, parent, false)
        return HomeHolder(binding,viewType)
    }

    override fun getItemCount() = 4

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        holder.rowHomeBinding.viewModel  = viewModel
        holder.rowHomeBinding.homeData  = homeData[position]
        holder.rowHomeBinding.imageView.setImageResource(images[position])
    }

    inner class HomeHolder(val rowHomeBinding: HomeItemBinding, viewType: Int) :
        RecyclerView.ViewHolder(rowHomeBinding.root) {
    }
}

