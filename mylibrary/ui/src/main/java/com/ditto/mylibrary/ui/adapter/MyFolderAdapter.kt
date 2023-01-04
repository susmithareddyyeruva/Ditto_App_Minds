package com.ditto.mylibrary.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.MyFolderData
import com.ditto.mylibrary.ui.MyFolderViewModel
import com.ditto.mylibrary.ui.R
import com.ditto.mylibrary.ui.databinding.ItemSingleMyfolderBinding

class MyFolderAdapter(context: Context, data: List<MyFolderData>?,private val renameListener: OnRenameListener,
private val deleteClicked: OnDeleteClicked) :
    RecyclerView.Adapter<MyFolderAdapter.MyFolderHolder>() {
    lateinit var viewModel: MyFolderViewModel
    private var items: List<MyFolderData>? = data
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var clickedPostion = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFolderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSingleMyfolderBinding.inflate(inflater, parent, false)
        return MyFolderHolder(binding, viewType)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyFolderHolder, position: Int) {
        holder.itemSingleMyfolderBinding.viewModel = viewModel
        holder.itemSingleMyfolderBinding.data = items?.get(position)
        val data = items?.get(position)
        /*  Glide.with(holder.itemSingleMyfolderBinding?.itemImage.context)
              .load(items?.get(position)?.url)
              .placeholder(com.ditto.mylibrary.ui.R.drawable.ic_placeholder)
              .into(holder.itemSingleMyfolderBinding.itemImage)*/

      //  Utility.increaseTouch(holder.itemSingleMyfolderBinding.imgIconClose,10f)

        holder.itemSingleMyfolderBinding.prodName.text = data?.title

        /*if(patterns.get(position).tailornovaDesignName.isNullOrEmpty()){
            holder.patternsItemBinding.textPatternName.text = patterns.get(position).prodName
        }else{
            holder.patternsItemBinding.textPatternName.text = patterns.get(position).tailornovaDesignName
        }*/

        if (data?.url != 0) {
            holder.itemSingleMyfolderBinding.itemImage.setImageResource(
                data?.url ?: 0
            )
        }
        holder.itemSingleMyfolderBinding.imgBg.setBackgroundColor(
            ContextCompat.getColor(
                holder.itemSingleMyfolderBinding.imgBg.context,
                R.color.white
            )
        )

        /*if (position == 0) {
            holder.itemSingleMyfolderBinding.imgBg.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemSingleMyfolderBinding.imgBg.context,
                    R.color.white
                )
            )
        } else if (position == 1) {
            holder.itemSingleMyfolderBinding.imgBg.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemSingleMyfolderBinding.imgBg.context,
                    R.color.white
                )
            )
        }else{
            holder.itemSingleMyfolderBinding.imgBg.setBackgroundColor(ContextCompat.getColor(
                holder.itemSingleMyfolderBinding.imgBg.context,
                R.color.white
            ))
        }*/

        holder.itemSingleMyfolderBinding.proAction.setOnClickListener {  //More
            //  data?.isAction = !(data?.isAction ?: false)

            clickedPostion = position
            data?.clicked = clickedPostion == position
            notifyDataSetChanged()
            holder.itemSingleMyfolderBinding.rootView.isClickable =false
            holder.itemSingleMyfolderBinding.rootView.isFocusable =false
        }


        holder.itemSingleMyfolderBinding.renameText.setOnClickListener {

            Log.d("Testing", ">>>>>>   Myfolder Rename ")
            holder.itemSingleMyfolderBinding.layoutDialog.visibility = View.GONE
            renameListener.onRenameClicked(data?.title?:"")

        }
        holder.itemSingleMyfolderBinding.deleteText.setOnClickListener {

            Log.d("Testing", ">>>>>>   Myfolder Delete ")
            holder.itemSingleMyfolderBinding.layoutDialog.visibility = View.GONE
            deleteClicked.onDeleteClicked(data?.title?:"")

        }
        holder.itemSingleMyfolderBinding.rootView.setOnClickListener {
            if (position==0){
               viewModel.createFolderEvent()
            }else{
                viewModel.navigateToFolderDetails(data?.title?:"")
            }
        }
        if (clickedPostion!=position){
            data?.clicked=false
        }
        holder.itemSingleMyfolderBinding.imgIconClose.setOnClickListener {
            holder.itemSingleMyfolderBinding.layoutDialog.visibility = View.GONE
            holder.itemSingleMyfolderBinding.rootView.isClickable =true
            holder.itemSingleMyfolderBinding.rootView.isFocusable =true
        }
      //  holder.itemSingleMyfolderBinding.rootView.isClickable = !holder.itemSingleMyfolderBinding.layoutDialog.isVisible


    }

    inner class MyFolderHolder(
        val itemSingleMyfolderBinding: ItemSingleMyfolderBinding,
        viewType: Int
    ) :
        RecyclerView.ViewHolder(itemSingleMyfolderBinding.root) {
    }
    interface OnRenameListener{
        fun onRenameClicked(s: String)
    }
    interface OnDeleteClicked{
        fun onDeleteClicked(title: String)
    }
}