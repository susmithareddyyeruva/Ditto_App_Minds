package com.ditto.menuitems_ui.faq.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.faq.ui.models.SubAnswModel

class SubquesAdapter (context: Context, data: List<SubAnswModel>?) :
    RecyclerView.Adapter<SubquesAdapter.SubquesViewHolder>() {

    private var items: List<SubAnswModel>? = data
    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubquesAdapter.SubquesViewHolder {
        val view = inflater.inflate(R.layout.faq_sub_desc_item, parent, false)
        return SubquesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size?:0
    }

    override fun onBindViewHolder(holder: SubquesAdapter.SubquesViewHolder, position: Int) {
        val item = items?.get(position)
        holder.tvTitle.text = item?.title
        holder.tvDescription.text = item?.description
    }
    class SubquesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.text_sub_head)
        var tvDescription: TextView = itemView.findViewById(R.id.text_sub_answ)
    }
}