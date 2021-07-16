package com.ditto.mylibrary.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.FilterItems
import com.ditto.mylibrary.ui.AllPatternsViewModel
import com.ditto.mylibrary.ui.databinding.ItemFilterdetailsBinding

class FilterDetailsAdapter(
    private var ItemsListener: SelectedItemsListener,
    private var menuList: HashMap<String, ArrayList<FilterItems>>,
    private var keys: String
) :
    RecyclerView.Adapter<FilterDetailsAdapter.FilterCategoriesHolder>() {

    lateinit var viewModel: AllPatternsViewModel
    private var viewGroup: ViewGroup? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterCategoriesHolder {
        viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFilterdetailsBinding.inflate(inflater, parent, false)
        return FilterCategoriesHolder(binding, viewType)
    }

    override fun getItemCount() = menuList[keys]?.toList()?.size?:0

    override fun onBindViewHolder(holder: FilterCategoriesHolder, position: Int) {
        val filterItems=menuList[keys]?.toList()
        holder.itemFilterdetailsBinding.filter = filterItems?.get(position)
        holder.itemFilterdetailsBinding.viewModel = viewModel
        holder.itemFilterdetailsBinding.checkItem.isChecked = filterItems?.get(position)?.isSelected?:false
        holder.itemView.setOnClickListener {
            holder.itemFilterdetailsBinding.checkItem.performClick()
        }
        holder.itemFilterdetailsBinding.checkItem.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                Log.d("Checked true", filterItems?.get(position)?.title?:"")
                filterItems?.get(position)?.isSelected = true
                ItemsListener.onItemsSelected(filterItems?.get(position)?.title?:"", true)
            } else {
                filterItems?.get(position)?.isSelected = false
                compoundButton.isChecked = false
                Log.d("Checked false", filterItems?.get(position)?.title?:"")
                ItemsListener.onItemsSelected(filterItems?.get(position)?.title?:"", false)
            }
        }

    }

    inner class FilterCategoriesHolder(
        val itemFilterdetailsBinding: ItemFilterdetailsBinding,
        viewType: Int
    ) :
        RecyclerView.ViewHolder(itemFilterdetailsBinding.root) {
    }

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



