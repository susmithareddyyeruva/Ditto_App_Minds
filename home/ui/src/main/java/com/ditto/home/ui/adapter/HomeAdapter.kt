package com.ditto.home.ui.adapter

import android.content.Context
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
class HomeAdapter(private val context: Context) : RecyclerView.Adapter<HomeAdapter.HomeHolder>(),
    BindableAdapter<List<HomeData>> {

    lateinit var viewModel: HomeViewModel
    private var homeData: List<HomeData> = emptyList()
    val images = intArrayOf(
        R.drawable.grey_background_rectangle,  R.drawable.ic_home_pattern_library_new_home_design, R.drawable.ic_home_ditto_com,
        R.drawable.ic_home_joann_new_design)

    override fun setListData(items: List<HomeData>) {
        homeData = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = HomeItemBinding.inflate(inflater, parent, false)
        return HomeHolder(binding,viewType)
    }

    override fun getItemCount() = homeData.size

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        holder.rowHomeBinding.viewModel  = viewModel
        holder.rowHomeBinding.homeData  = homeData[position]
        holder.rowHomeBinding.imageView.setImageResource(images[position])
        holder.rowHomeBinding.textPatternHeader.setText(context.getString(R.string.pattern_library_count1,viewModel.productCount))

    }

    inner class HomeHolder(val rowHomeBinding: HomeItemBinding, viewType: Int) :
        RecyclerView.ViewHolder(rowHomeBinding.root) {

    }
}

