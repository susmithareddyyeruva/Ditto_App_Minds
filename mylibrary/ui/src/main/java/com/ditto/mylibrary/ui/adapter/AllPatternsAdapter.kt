package com.ditto.mylibrary.ui.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.ui.AllPatternsViewModel
import com.ditto.mylibrary.ui.R
import com.ditto.mylibrary.ui.databinding.AllPatternSinglelayoutBinding
import core.appstate.AppState
import core.binding.BindableAdapter

class AllPatternsAdapter : RecyclerView.Adapter<AllPatternsAdapter.PatternHolder>(),
    BindableAdapter<List<ProdDomain>> {

    lateinit var viewModel: AllPatternsViewModel
    private var patterns: List<ProdDomain> = emptyList()
    private var viewGroup: ViewGroup? = null

    override fun setListData(items: List<ProdDomain>) {
        patterns = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternHolder {
        viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = AllPatternSinglelayoutBinding.inflate(inflater, parent, false)
        return PatternHolder(binding, viewType)
    }

    override fun getItemCount() = patterns.size

    override fun onBindViewHolder(holder: PatternHolder, position: Int) {
        holder.patternsItemBinding.product = patterns[position]
        holder.patternsItemBinding.viewModel = viewModel
       // Utility.increaseTouch(holder.patternsItemBinding.imageAdd,10f)

        val res: Resources = viewGroup!!.resources
        Glide.with(holder.patternsItemBinding?.imagePattern.context)
            .load(patterns.get(position).image)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.patternsItemBinding.imagePattern)

        if (AppState.getIsLogged()) {
            if (position == 0) {
                holder.patternsItemBinding.likeImage.setImageResource(R.drawable.ic_like)
            } else {
                holder.patternsItemBinding.likeImage.setImageResource(R.drawable.ic_fav_bgred)
            }
            holder.patternsItemBinding.textviewPatternType.visibility = View.VISIBLE
            if (patterns[position].status?.toUpperCase()
                    .equals("NEW") || patterns[position].status?.toUpperCase().equals("OWNED")
            ) {
                holder.patternsItemBinding.textviewPatternType.setBackgroundColor(
                    (ContextCompat.getColor(
                        holder.patternsItemBinding.textviewPatternType.context,
                        R.color.text_new
                    ))
                )

                holder.patternsItemBinding.textviewPatternType.visibility = View.VISIBLE
                holder.patternsItemBinding.textviewPatternType.text =
                    patterns[position].status?.toUpperCase()
            } else if (patterns[position].status?.toUpperCase().equals("EXPIRED")) {
                holder.patternsItemBinding.textviewPatternType.visibility = View.VISIBLE
                holder.patternsItemBinding.textviewPatternType.text =
                    patterns[position].status?.toUpperCase()
                holder.patternsItemBinding.textviewPatternType.setBackgroundColor(
                    (ContextCompat.getColor(
                        holder.patternsItemBinding.textviewPatternType.context,
                        R.color.text_expired
                    ))
                )
            } else {
                holder.patternsItemBinding.textviewPatternType.visibility = View.GONE

            }


        } else { //Guest User

        }
    }

    inner class PatternHolder(
        val patternsItemBinding: AllPatternSinglelayoutBinding,
        viewType: Int
    ) :
        RecyclerView.ViewHolder(patternsItemBinding.root) {
    }
}

