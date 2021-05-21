package com.ditto.mylibrary.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.FilterMenuData
import com.ditto.mylibrary.ui.R
import kotlinx.android.synthetic.main.item_singlecategory.view.*

class FilterRvAdapter(private var items: ArrayList<FilterMenuData>, private var currentPos: Int) :RecyclerView.Adapter<FilterRvAdapter.NavigationItemViewHolder>() {

    private lateinit var context: Context

    class NavigationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationItemViewHolder {
        context = parent.context
        val navItem = LayoutInflater.from(parent.context).inflate(R.layout.item_singlecategory, parent, false)
        return NavigationItemViewHolder(navItem)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: NavigationItemViewHolder, position: Int) {
        // To highlight the selected Item, show different background color
        if (position == currentPos) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            holder.itemView.itemCategoryName.setTextColor(ContextCompat.getColor(context, R.color.black))
            val typeface = ResourcesCompat.getFont(context, R.font.avenir_next_lt_pro_demi)
            holder.itemView.itemCategoryName.setTypeface(typeface)
            holder.itemView.imgNext.visibility=View.VISIBLE
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.cell_color))
            holder.itemView.itemCategoryName.setTextColor(ContextCompat.getColor(context, R.color.black))
            val typeface = ResourcesCompat.getFont(context, R.font.avenir_next_lt_pro_regular)
            holder.itemView.itemCategoryName.setTypeface(typeface)
            holder.itemView.imgNext.visibility=View.GONE
        }

        holder.itemView.itemCategoryName.text = items[position].title
    }
}