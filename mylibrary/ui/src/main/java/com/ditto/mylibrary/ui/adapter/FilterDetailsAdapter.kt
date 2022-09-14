package com.ditto.mylibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.FilterItems
import com.ditto.mylibrary.ui.MyLibraryViewModel
import com.ditto.mylibrary.ui.R
import com.ditto.mylibrary.ui.databinding.ItemFilterdetailsBinding
import java.util.*
import kotlin.collections.ArrayList

class FilterDetailsAdapter(
    private var itemsListener: SelectedItemsListener,
    private var menuList: TreeMap<String, ArrayList<FilterItems>>,
    private var keys: String
) :
    RecyclerView.Adapter<FilterDetailsAdapter.FilterCategoriesHolder>() {

    lateinit var viewModel: MyLibraryViewModel
    private var viewGroup: ViewGroup? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterCategoriesHolder {
        viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFilterdetailsBinding.inflate(inflater, parent, false)
        return FilterCategoriesHolder(binding, viewType)
    }

    override fun getItemCount() = menuList[keys]?.toList()?.size ?: 0

    override fun onBindViewHolder(holder: FilterCategoriesHolder, position: Int) {
        val filterItems = menuList[keys]?.toList()
        holder.itemFilterdetailsBinding.filter = filterItems?.get(position)
        holder.itemFilterdetailsBinding.viewModel = viewModel
        if (filterItems?.get(position)?.isSelected == true) {
            holder.itemFilterdetailsBinding.checkItem.background = ContextCompat.getDrawable(
                holder.itemFilterdetailsBinding.checkItem.context,
                R.drawable.ic_chk_select
            )
        } else {
            holder.itemFilterdetailsBinding.checkItem.background = ContextCompat.getDrawable(
                holder.itemFilterdetailsBinding.checkItem.context,
                R.drawable.ic_check
            )
        }
        holder.itemView.setOnClickListener {
            //holder.itemFilterdetailsBinding.checkItem.performClick()
            filterItems?.get(position)?.isSelected =
                !(filterItems?.get(position)?.isSelected ?: false)
            notifyDataSetChanged()
        }

    }

    inner class FilterCategoriesHolder(
        val itemFilterdetailsBinding: ItemFilterdetailsBinding,
        viewType: Int
    ) :
        RecyclerView.ViewHolder(itemFilterdetailsBinding.root)

    interface SelectedItemsListener {
        fun onItemsSelected(title: String, isSelected: Boolean)
    }

    fun updateList(
        menuKey: String
    ) {
        this.keys = menuKey
        notifyDataSetChanged()

    }
}



