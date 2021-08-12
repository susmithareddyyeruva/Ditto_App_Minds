package com.ditto.mylibrary.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ditto.mylibrary.domain.model.MyFolderData
import com.ditto.mylibrary.ui.MyLibraryViewModel
import com.ditto.mylibrary.ui.databinding.ItemSingleMyfolderBinding

class MyFolderAdapter(context: Context, data: List<MyFolderData>?) :
    RecyclerView.Adapter<MyFolderAdapter.MyFolderHolder>() {
    lateinit var viewModel: MyLibraryViewModel
    private var items: List<MyFolderData>? = data
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFolderHolder {
        val viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSingleMyfolderBinding.inflate(inflater, parent, false)
        return MyFolderHolder(binding, viewType)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyFolderHolder, position: Int) {
        holder.itemSingleMyfolderBinding.viewModel = viewModel
        Glide.with(holder.itemSingleMyfolderBinding?.imgBg.context)
            .load(items?.get(position)?.url)
            .placeholder(com.ditto.mylibrary.ui.R.drawable.ic_placeholder)
            .into(holder.itemSingleMyfolderBinding.imgBg)

        holder.itemSingleMyfolderBinding.prodName.text = items?.get(position)?.title
        /*      if (items?.get(position)?.isAction == true){
                  holder.itemSingleMyfolderBinding.proAction.visibility=View.VISIBLE
              }else
                  holder.itemSingleMyfolderBinding.proAction.visibility=View.GONE*/

        holder.itemSingleMyfolderBinding.proAction.setOnClickListener {  //More
            items?.get(position)?.isAction=!(items?.get(position)?.isAction?:false)
            if (items?.get(position)?.isAction == true) {

                holder.itemSingleMyfolderBinding.layoutDialog.visibility = View.VISIBLE
            } else {
                holder.itemSingleMyfolderBinding.layoutDialog.visibility = View.GONE
            }

        }
        holder.itemSingleMyfolderBinding.imgIcon.setOnClickListener {
            holder.itemSingleMyfolderBinding.layoutDialog.visibility = View.GONE
        }

    }

    inner class MyFolderHolder(
        val itemSingleMyfolderBinding: ItemSingleMyfolderBinding,
        viewType: Int
    ) :
        RecyclerView.ViewHolder(itemSingleMyfolderBinding.root) {
    }
}