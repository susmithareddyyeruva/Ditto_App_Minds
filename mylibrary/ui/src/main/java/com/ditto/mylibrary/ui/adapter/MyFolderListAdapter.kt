package com.ditto.mylibrary.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.MyFolderList
import com.ditto.mylibrary.ui.AllPatternsViewModel
import com.ditto.mylibrary.ui.R
import com.ditto.mylibrary.ui.databinding.ItemFoldersBinding

class MyFolderListAdapter(
    context: Context,
    data: List<MyFolderList>?,
    private val createFolderListener: CreateFolderListener,
    private val folderClicked: OnItemFolderClicked
) :
    RecyclerView.Adapter<MyFolderListAdapter.MyFolderListHolder>() {
    lateinit var viewModel: AllPatternsViewModel
    private var items: List<MyFolderList>? = data

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyFolderListHolder {
        val viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFoldersBinding.inflate(inflater, parent, false)
        return MyFolderListHolder(binding, viewType)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyFolderListHolder, position: Int) {
        holder.itemFoldersBinding.viewModel = viewModel
        holder.itemFoldersBinding.data = items?.get(position)
        val data = items?.get(position)
        holder.itemFoldersBinding.textdialogHeading.text = data?.folderName
        if (position == 0) {
            holder.itemFoldersBinding.textdialogHeading.setTextColor(
                ContextCompat.getColor(
                    holder.itemFoldersBinding.dialogHeading.context,
                    R.color.app_red
                )
            )
            holder.itemFoldersBinding.folderStatus.setImageResource(R.drawable.ic_red_new)
        } else {
            holder.itemFoldersBinding.textdialogHeading.setTextColor(
                ContextCompat.getColor(
                    holder.itemFoldersBinding.dialogHeading.context,
                    R.color.black
                )
            )
            holder.itemFoldersBinding.folderStatus.setImageResource(R.drawable.ic_folder_grey)
        }
        holder.itemFoldersBinding.dialogHeading.setOnClickListener {
            if (position == 0) {
                createFolderListener.onNewFolderClicked()
            }else{
                folderClicked.onItemClicked(data?.folderName?:"")
            }
        }

    }

    inner class MyFolderListHolder(
        val itemFoldersBinding: ItemFoldersBinding,
        viewType: Int
    ) :
        RecyclerView.ViewHolder(itemFoldersBinding.root) {
    }

    interface CreateFolderListener {
        fun onNewFolderClicked()
    }

    interface OnItemFolderClicked {
        fun onItemClicked(itemName:String)
    }
}