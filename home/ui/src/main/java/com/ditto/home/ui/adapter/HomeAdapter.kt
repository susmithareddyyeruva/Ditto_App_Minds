package com.ditto.home.ui.adapter

import android.view.LayoutInflater
import android.view.View
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
    BindableAdapter<String> {

    private val images = intArrayOf(R.drawable.ic_home_pattern_library,R.drawable.ic_home_ditto,
        R.drawable.ic_home_joann,R.drawable.ic_home_joann)

    private val title = intArrayOf(R.string.pattern_library,R.string.more_patterns_available_at,
        R.string.for_fine_crafts_and_fabrics_visit_our_site,R.string.beam_setup_and_calibration)

    private val description = intArrayOf(R.string.all_your_patterns_in_one_place,R.string.ditto_patterns_site,
        R.string.joann_site,R.string.view_tutorial)

    lateinit var viewModel: HomeViewModel
    private var homeData: String = ""

    override fun setListData(items: String) {
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
        holder.rowHomeBinding.viewModel = viewModel
        holder.rowHomeBinding.imageView.setImageResource(images[position])
        holder.rowHomeBinding.textPatternHeader.setText(title[position])
        holder.rowHomeBinding.textHeader.setText(title[position])
        holder.rowHomeBinding.textDesc.setText(description[position])
        if(position==0){
            holder.rowHomeBinding.groupNonPattern.visibility = View.GONE
            holder.rowHomeBinding.groupPattern.visibility =View.VISIBLE
        }else{
            holder.rowHomeBinding.groupNonPattern.visibility = View.VISIBLE
            holder.rowHomeBinding.groupPattern.visibility =View.GONE
        }
    }

    inner class HomeHolder(val rowHomeBinding: HomeItemBinding, viewType: Int) :
        RecyclerView.ViewHolder(rowHomeBinding.root) {
    }
}

