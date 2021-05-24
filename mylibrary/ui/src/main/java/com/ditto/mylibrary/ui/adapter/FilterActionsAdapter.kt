package com.ditto.mylibrary.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.FilterItems
import com.ditto.mylibrary.ui.R
import kotlinx.android.synthetic.main.item_filter.view.*

class FilterActionsAdapter(private var items: ArrayList<FilterItems>, private var ItemsListener:SelectedItemsListener) :RecyclerView.Adapter<FilterActionsAdapter.NavigationItemViewHolder>() {

    private lateinit var context: Context
    private var menuItem:String=""

    class NavigationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationItemViewHolder {
        context = parent.context
        val navItem = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return NavigationItemViewHolder(navItem)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: NavigationItemViewHolder, position: Int) {
        holder.itemView.itemAction.text = items[position].title
        holder.itemView.checkItem.isChecked=items[position].isSelected
        holder.itemView.checkItem.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
              Log.d("Checked true",items[position].title)
                items[position].isSelected=true
                ItemsListener.onItemsSelected(items[position].title,true,menuItem)
            } else {
                items[position].isSelected=false
                compoundButton.isChecked = false
                Log.d("Checked false",items[position].title)
                ItemsListener.onItemsSelected(items[position].title,false,menuItem)
            }
        }


    }
    fun updateList(
        filterList: ArrayList<FilterItems>,
        menu: String
    ) {
        this.items=filterList
        this.menuItem=menu
        notifyDataSetChanged()

    }
    interface SelectedItemsListener{
        fun onItemsSelected(title: String, isSelected: Boolean,menu: String)
    }
}