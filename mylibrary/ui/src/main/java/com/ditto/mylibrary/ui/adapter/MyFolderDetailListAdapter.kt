package com.ditto.mylibrary.ui.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.ui.MyFolderViewModel
import com.ditto.mylibrary.ui.R
import com.ditto.mylibrary.ui.databinding.MyfolderSingleitemBinding
import core.appstate.AppState
import core.binding.BindableAdapter

class MyFolderDetailListAdapter : RecyclerView.Adapter<MyFolderDetailListAdapter.FolderHolder>(),
    BindableAdapter<List<ProdDomain>> {

    lateinit var viewModel: MyFolderViewModel
    private var patterns: List<ProdDomain> = emptyList()
    private var viewGroup: ViewGroup? = null

    override fun setListData(items: List<ProdDomain>) {
        patterns = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderHolder {
        viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = MyfolderSingleitemBinding.inflate(inflater, parent, false)
        return FolderHolder(binding, viewType)
    }

    override fun getItemCount() = patterns.size

    override fun onBindViewHolder(holder: FolderHolder, position: Int) {
        holder.patternsItemBinding.product = patterns[position]
        holder.patternsItemBinding.viewModel = viewModel
        val data=patterns[position]

        val res: Resources = viewGroup!!.resources
        Glide.with(holder.patternsItemBinding?.imagePattern.context)
            .load(patterns.get(position).image)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.patternsItemBinding.imagePattern)

        if (AppState.getIsLogged()) {
            if (data.isFavourite == true) {
                holder.patternsItemBinding.likeImage.setImageResource(R.drawable.ic_like)
            } else {
                holder.patternsItemBinding.likeImage.setImageResource(R.drawable.ic_fav_bgred)
            }
        } else { //Guest User

        }
        holder.patternsItemBinding.textviewPatternType.setBackgroundColor(
            (ContextCompat.getColor(
                holder.patternsItemBinding.textviewPatternType.context,
                R.color.text_new
            ))
        )
        if (patterns[position].patternType?.toUpperCase().equals("TRIAL") == true) {
            holder.patternsItemBinding.textviewPatternType.text =
                patterns[position].patternType?.toUpperCase()

        } else {
            //Pattern type which is not trial pattern
            holder.patternsItemBinding.textviewPatternType.visibility = View.VISIBLE
            if (patterns[position].status?.toUpperCase()
                    .equals("NEW") || patterns[position].status?.toUpperCase().equals("OWNED")
                || patterns[position].status?.toUpperCase()
                    .equals("TRIAL") || patterns[position].status?.toUpperCase()
                    .equals("SUBSCRIBED")
            ) {
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
        }
    }

    inner class FolderHolder(
        val patternsItemBinding: MyfolderSingleitemBinding,
        viewType: Int
    ) :
        RecyclerView.ViewHolder(patternsItemBinding.root) {
    }
}