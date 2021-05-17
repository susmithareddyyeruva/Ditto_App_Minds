package com.ditto.menuitems_ui.faq.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.faq.ui.models.FAQModel


class FAQAdapter (context: Context, data: List<FAQModel>?) :
    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {
    private var mContext: Context = context
    private var items: List<FAQModel>? = data
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var subquesAdapter:SubquesAdapter? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override
    fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val item = items?.get(position)
        holder.tvques.text = item?.Ques
        val htmlAsSpanned = Html.fromHtml(item?.Answ)
        holder.tvAnsw.text = htmlAsSpanned
        if (item?.SubAnsw?.size!! > 0){
            holder.rvsubques.visibility = View.VISIBLE
            subquesAdapter = SubquesAdapter(mContext,item?.SubAnsw)
            holder.rvsubques.adapter = subquesAdapter
            holder.rvsubques.layoutManager =LinearLayoutManager(mContext)
        } else {
            holder.rvsubques.visibility = View.GONE
        }
        holder.linheader.setOnClickListener { onItemClicked(item,position) }
        if (item?.isExpanded!!) {
            holder.relparent.background = mContext.getDrawable(R.drawable.border_layout_white)
            holder.tvAnsw.visibility = View.VISIBLE
            holder.rvsubques.visibility = View.VISIBLE
            holder.ivArrow.setImageResource(R.drawable.ic_uparrow)
        } else {
            holder.relparent.background = mContext.getDrawable(R.drawable.border_layout)
            holder.tvAnsw.visibility = View.GONE
            holder.rvsubques.visibility = View.GONE
            holder.ivArrow.setImageResource(R.drawable.ic_down_arrow)
        }
        holder.tvAnsw.setOnClickListener {
            if (position == 0){
                val url = mContext.getString(R.string.str_patterns_url)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                mContext.startActivity(i)
            }
        }

    }
    private fun onItemClicked(faqModel: FAQModel?,pos : Int) {
        faqModel?.isExpanded = !faqModel?.isExpanded!!

        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQAdapter.FAQViewHolder {
        val view = inflater.inflate(R.layout.faq_desc_item, parent, false)
        return FAQViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvques: TextView = itemView.findViewById(R.id.tv_ques)
        var tvAnsw: TextView = itemView.findViewById(R.id.tv_desc)
        var rvsubques: RecyclerView = itemView.findViewById(R.id.rv_subdesc)
        var ivArrow: ImageView = itemView.findViewById(R.id.iv_icon)
        var linheader: LinearLayout = itemView.findViewById(R.id.header)
        var relparent: RelativeLayout = itemView.findViewById(R.id.parentlay)
    }
}