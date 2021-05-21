package com.ditto.mylibrary.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.FilterResultData
import com.ditto.mylibrary.ui.R
import kotlinx.android.synthetic.main.item_filter.view.*

class FilterActionsAdapter(private var items: ArrayList<FilterResultData>) :RecyclerView.Adapter<FilterActionsAdapter.NavigationItemViewHolder>() {

    private lateinit var context: Context

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


    }
    fun updateList(filterList: ArrayList<FilterResultData>) {
        this.items=filterList
        notifyDataSetChanged()

    }
}