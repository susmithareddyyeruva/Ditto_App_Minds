package com.ditto.home.ui.adapter

import android.content.Context
import android.graphics.Paint
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
        R.drawable.my_patterns_tile, R.drawable.tutorials_tile, R.drawable.more_patterns_tile,
        R.drawable.joann_tile)

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
        /*if (homeData[position].id==1) {
            holder.rowHomeBinding.textHeader.setText(context.getString(R.string.pattern_library_count1,viewModel.productCount))
        }else{
            holder.rowHomeBinding.textHeader.setText(homeData[position].title)
        }*/
        //add new title text as per rebranding
        when(homeData[position].id) {
            0 -> {
                holder.rowHomeBinding.tileDescription.text =
                    String.format(context.getString(R.string.my_patterns_title_count),viewModel.productCount)
                holder.rowHomeBinding.tileDescription.setTextColor(context.resources.getColor(R.color.white))
            }
            1 -> {
                holder.rowHomeBinding.tileDescription.text = context.getString(R.string.tutorials_title_text)
                holder.rowHomeBinding.tileDescription.setTextColor(context.resources.getColor(R.color.needle_grey))
            }
            2 -> {
                holder.rowHomeBinding.tileDescription.text = context.getString(R.string.ditto_patterns_site)
                holder.rowHomeBinding.tileDescription.setTextColor(context.resources.getColor(R.color.needle_grey))
            }
            3 -> {
                holder.rowHomeBinding.tileDescription.text = context.getString(R.string.joann_site)
                holder.rowHomeBinding.tileDescription.setTextColor(context.resources.getColor(R.color.white))
            }
        }
        holder.rowHomeBinding.tileDescription.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    inner class HomeHolder(val rowHomeBinding: HomeItemBinding, viewType: Int) :
        RecyclerView.ViewHolder(rowHomeBinding.root) {

    }
}

