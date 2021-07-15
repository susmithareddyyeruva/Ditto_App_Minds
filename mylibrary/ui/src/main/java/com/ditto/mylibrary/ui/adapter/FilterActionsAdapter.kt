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

class FilterActionsAdapter(
  /*  private var items: List<FilterItems>,*/
    private var ItemsListener: SelectedItemsListener,
    private var menuList: HashMap<String, ArrayList<FilterItems>>,
    private var keys: String
) : RecyclerView.Adapter<FilterActionsAdapter.NavigationItemViewHolder>() {

    private lateinit var context: Context
    private var menuItem: String = ""

    class NavigationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationItemViewHolder {
        context = parent.context
        val navItem =
            LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return NavigationItemViewHolder(navItem)
    }

    override fun getItemCount() = menuList[keys]?.toList()?.size?:0

    override fun onBindViewHolder(holder: NavigationItemViewHolder, position: Int) {
        val result=menuList[keys]?.toList()
        holder.itemView.itemAction.text = result?.get(position)?.title
        holder.itemView.checkItem.isChecked =  result?.get(position)?.isSelected?:false
        holder.itemView.setOnClickListener {
            holder.itemView.checkItem.performClick()
          /*  items[position].isSelected=!items[position].isSelected
            notifyDataSetChanged()*/
        }
        holder.itemView.checkItem.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                Log.d("Checked true", result?.get(position)?.title?:"")
                result?.get(position)?.isSelected = true
                ItemsListener.onItemsSelected(result?.get(position)?.title?:"", true, menuItem)
            } else {
                result?.get(position)?.isSelected = false
                compoundButton.isChecked = false
                Log.d("Checked false", result?.get(position)?.title?:"")
                ItemsListener.onItemsSelected(result?.get(position)?.title?:"", false, menuItem)
            }
        }


    }

    fun updateList(
        menuKey: String
    ) {
        this.keys=menuKey
        notifyDataSetChanged()

    }


    interface SelectedItemsListener {
        fun onItemsSelected(title: String, isSelected: Boolean, menu: String)
    }


}