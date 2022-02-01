package com.ditto.mylibrary.ui.adapter

import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.ui.AllPatternsViewModel
import com.ditto.mylibrary.ui.R
import com.ditto.mylibrary.ui.databinding.MyLibraryPatternsItemBinding
import core.appstate.AppState
import core.binding.BindableAdapter


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
        if (!patterns.get(position).thumbnailImagePath.equals("")){
            val resID: Int = res.getIdentifier(patterns.get(position).thumbnailImagePath, "drawable", viewGroup!!.context.getPackageName())
           // val drawable: Drawable = res.getDrawable(resID)
            val drawable: Drawable? = ContextCompat.getDrawable(holder.patternsItemBinding.imagePattern.context,resID)
            val bitmap = (drawable as BitmapDrawable).bitmap
            holder.patternsItemBinding.imagePattern.setImageBitmap(bitmap)
        }

        if (AppState.getIsLogged()) {
            if (position == 0 ) {
                holder.patternsItemBinding.textviewPatternType.text = "NEW"
                holder.patternsItemBinding.textviewPatternType.setBackgroundColor(
                    (ContextCompat.getColor(
                        holder.patternsItemBinding.textviewPatternType.context,
                        R.color.text_new
                    ))
                )
            } else if (position == 1 ) {
                holder.patternsItemBinding.textviewPatternType.text = "OWNED"
                holder.patternsItemBinding.textviewPatternType.setBackgroundColor(
                    (ContextCompat.getColor(
                        holder.patternsItemBinding.textviewPatternType.context,
                        R.color.text_new
                    ))
                )
            } else if (position == 2) {
                holder.patternsItemBinding.textviewPatternType.text = "EXPIRED"
                holder.patternsItemBinding.textviewPatternType.setBackgroundColor(
                    (ContextCompat.getColor(
                        holder.patternsItemBinding.textviewPatternType.context,
                        R.color.text_expired
                    ))
                )
            }else if (position == 3 ) {
                holder.patternsItemBinding.textviewPatternType.visibility = View.GONE
            }else{
                holder.patternsItemBinding.textviewPatternType.text = "NEW"
                holder.patternsItemBinding.textviewPatternType.setBackgroundColor(
                    (ContextCompat.getColor(
                        holder.patternsItemBinding.textviewPatternType.context,
                        R.color.text_new
                    ))
                )
            }

        } else { //Guest User

        }
    }

    inner class PatternHolder(val patternsItemBinding: MyLibraryPatternsItemBinding, viewType: Int) :
        RecyclerView.ViewHolder(patternsItemBinding.root) {
    }
}

