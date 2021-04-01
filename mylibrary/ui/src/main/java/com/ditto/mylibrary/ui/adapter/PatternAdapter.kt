package com.ditto.mylibrary.ui.adapter

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.ui.AllPatternsViewModel
import core.binding.BindableAdapter
import com.ditto.mylibrary.ui.databinding.MyLibraryPatternsItemBinding


/**
 * Adapter class to map List<MyLibraryData> with RecyclerView
 */
class PatternAdapter : RecyclerView.Adapter<PatternAdapter.PatternHolder>(),
    BindableAdapter<List<MyLibraryData>> {

    lateinit var viewModel: AllPatternsViewModel
    private var patterns: List<MyLibraryData> = emptyList()
    private var viewGroup: ViewGroup? =null

    override fun setListData(items: List<MyLibraryData>) {
        patterns = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternHolder {
        viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = MyLibraryPatternsItemBinding.inflate(inflater, parent, false)
        return PatternHolder(binding,viewType)
    }

    override fun getItemCount() = patterns.size

    override fun onBindViewHolder(holder: PatternHolder, position: Int) {
        holder.patternsItemBinding.patternValue = patterns[position]
        holder.patternsItemBinding.viewModel = viewModel

        val res: Resources = viewGroup!!.resources
//        println("ImagefromDB${patterns.get(position).thumbnailImagePath}")
        if (!patterns.get(position).thumbnailImagePath.equals("")){
            val resID: Int = res.getIdentifier(patterns.get(position).thumbnailImagePath, "drawable", viewGroup!!.context.getPackageName())
            val drawable: Drawable = res.getDrawable(resID)
            val bitmap = (drawable as BitmapDrawable).bitmap
            holder.patternsItemBinding.imagePattern.setImageBitmap(bitmap)
        }
    }

    inner class PatternHolder(val patternsItemBinding: MyLibraryPatternsItemBinding, viewType: Int) :
        RecyclerView.ViewHolder(patternsItemBinding.root) {
    }
}

