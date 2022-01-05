package com.ditto.menuitems_ui.faq.ui.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ditto.menuitems.domain.model.faq.FAQDomain
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.faq.ui.VisitSiteListener
import com.ditto.menuitems_ui.faq.ui.WatchVideoClickListener


class FAQAdapter(
    context: Context,
    data: List<FAQDomain>?,
    val watchVideoClickListener: WatchVideoClickListener,
    val visitSiteListener: VisitSiteListener
) :
    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {
    private var mContext: Context = context
    private var items: List<FAQDomain>? = data
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var subquesAdapter: SubquesAdapter? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override
    fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val item = items?.get(position)
        holder.tvques.text = item?.Ques
        val htmlAsSpanned = HtmlCompat.fromHtml(item?.Answ?:"",HtmlCompat.FROM_HTML_MODE_LEGACY)
        holder.tvAnsw.text = htmlAsSpanned

       /* if (item?.SubAnsw?.size!! > 0) {
            holder.rvsubques.visibility = View.VISIBLE
            subquesAdapter = SubquesAdapter(mContext, item?.SubAnsw)
            holder.rvsubques.adapter = subquesAdapter
            holder.rvsubques.layoutManager = LinearLayoutManager(mContext)

        } else {

            holder.rvsubques.visibility = View.GONE
        }*/
        holder.linheader.setOnClickListener { onItemClicked(item, position) }
        if (item?.isExpanded!!) {
            holder.relparent.background = ContextCompat.getDrawable(mContext,R.drawable.drop_shadow)
            holder.relparent.elevation = 15f
            holder.tvAnsw.visibility = View.VISIBLE
            if (item?.SubAnsw?.size!! > 0) {
                holder.rvsubques.visibility = View.VISIBLE
                subquesAdapter = SubquesAdapter(mContext, item?.SubAnsw)
                holder.rvsubques.adapter = subquesAdapter
                holder.rvsubques.layoutManager = LinearLayoutManager(mContext)

            } else {

                holder.rvsubques.visibility = View.GONE
            }
            holder.ivArrow.setImageResource(R.drawable.ic_dropdown_up)
            if (!item?.web_url.isNullOrEmpty()) {
                holder.visit.visibility = View.VISIBLE
            } else {
                holder.visit.visibility = View.GONE
            }
            if (!item?.video_url.isNullOrEmpty()) {
                holder.watch.visibility = View.VISIBLE
            } else {
                holder.visit.visibility = View.GONE
            }
            if (item?.web_url.isNullOrEmpty() && item?.video_url.isNullOrEmpty()) {
                holder.visit.visibility = View.GONE
                holder.visit.visibility = View.GONE
            }
            holder.visit.setOnClickListener {
                visitSiteListener.onVisitClick(item?.web_url ?: "")
            }
            holder.watch.setOnClickListener {
                watchVideoClickListener.onVideoClick(item?.video_url ?: "")
            }
        } else {
            holder.relparent.background = ContextCompat.getDrawable(mContext,R.drawable.border_layout)
            holder.tvAnsw.visibility = View.GONE
            holder.rvsubques.visibility = View.GONE
            holder.ivArrow.setImageResource(R.drawable.ic_dropdown_down)
            holder.watch.visibility = View.GONE
            holder.visit.visibility = View.GONE
        }
//        holder.tvAnsw.setOnClickListener {
//            if (position == 0) {
//                val url = mContext.getString(R.string.str_patterns_url)
//                val i = Intent(Intent.ACTION_VIEW)
//                i.data = Uri.parse(url)
//                mContext.startActivity(i)
//            }
//        }

    }

    private fun onItemClicked(faqModel: FAQDomain?, pos: Int) {
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
        var watch: RelativeLayout = itemView.findViewById(R.id.relWatch)
        var visit: RelativeLayout = itemView.findViewById(R.id.relVisit)
    }
}