package com.ditto.mylibrary.ui.adapter

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.ui.AllPatternsViewModel
import com.ditto.mylibrary.ui.databinding.ActiveProjectsItemBinding
import core.binding.BindableAdapter

class ActiveProjectAdapter : RecyclerView.Adapter<ActiveProjectAdapter.ActivePatternHolder>(),
    BindableAdapter<List<MyLibraryData>> {
    lateinit var viewModel: AllPatternsViewModel
    private var patterns: List<MyLibraryData> = emptyList()
    private var viewGroup: ViewGroup? =null

    override fun setListData(items: List<MyLibraryData>) {
        patterns = items
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivePatternHolder {
        viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = ActiveProjectsItemBinding.inflate(inflater, parent, false)
        return ActivePatternHolder(binding,viewType)
    }
    override fun getItemCount() = patterns.size

    inner class ActivePatternHolder(val patternsItemBinding: ActiveProjectsItemBinding, viewType: Int) :
        RecyclerView.ViewHolder(patternsItemBinding.root) {
    }

    override fun onBindViewHolder(holder: ActivePatternHolder, position: Int) {
        holder.patternsItemBinding.patternValue = patterns[position]
        holder.patternsItemBinding.viewModel = viewModel

        val res: Resources = viewGroup!!.resources
        holder.patternsItemBinding.seekbarStatus.isEnabled = false
        if (!patterns.get(position).thumbnailImagePath.equals("")){
            val resID: Int = res.getIdentifier(patterns.get(position).thumbnailImagePath, "drawable", viewGroup!!.context.getPackageName())
            val drawable: Drawable? = ContextCompat.getDrawable(holder.patternsItemBinding.imagePattern.context,resID)
            val bitmap = (drawable as BitmapDrawable).bitmap
            holder.patternsItemBinding.imagePattern.setImageBitmap(bitmap)
        }
    }
}