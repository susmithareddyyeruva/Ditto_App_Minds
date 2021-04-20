package com.ditto.workspace.ui.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.ditto.workspace.domain.model.DragData
import com.ditto.workspace.domain.model.PatternPieces
import com.ditto.workspace.ui.R
import com.ditto.workspace.ui.WorkspaceViewModel
import com.ditto.workspace.ui.databinding.PatternsPiecesItemBinding
import com.ditto.workspace.ui.util.Draggable
import core.binding.BindableAdapter
import core.ui.common.Utility


/**
 * Adapter class to map List<PatternPieces> with RecyclerView
 */
class PatternPiecesAdapter() : RecyclerView.Adapter<PatternPiecesAdapter.PatternPieceHolder>(),
    BindableAdapter<List<PatternPieces>> {

    lateinit var viewModel: WorkspaceViewModel
    private var patternPieces: MutableList<PatternPieces> = ArrayList()
    private var viewGroup: ViewGroup? = null

    override fun setListData(listData: List<PatternPieces>) {
        patternPieces = listData.filter { it.tabCategory == viewModel.tabCategory }.toMutableList()
            .also { list -> list.sortBy { it.positionInTab.toInt() } }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternPieceHolder {
        viewGroup = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = PatternsPiecesItemBinding.inflate(inflater, parent, false)
        return PatternPieceHolder(binding, viewType)
    }

    override fun getItemCount() = patternPieces.size

    override fun onBindViewHolder(holder: PatternPieceHolder, position: Int) {
        holder.patternsPiecesBinding.viewModel = viewModel
        holder.patternsPiecesBinding.imageArrow.visibility = View.GONE
        println("ImagefromDB${patternPieces.get(position).imagePath}")
        if (!patternPieces.get(position).imagePath.equals("")) {
            val drawable = Utility.getDrawableFromString(
                viewGroup!!.context,
                patternPieces.get(position).imagePath
            )
            holder.patternsPiecesBinding.imageView.setImageDrawable(drawable)
            if(patternPieces[position].splice == "YES") {
                if(patternPieces[position].spliceDirection == "Splice Left-to-Right") {
                    holder.patternsPiecesBinding.imageArrow.visibility = View.VISIBLE
                    val arrowDrawable = Utility.getDrawableFromString(
                        viewGroup!!.context,
                        "ic_arrow_horizontal"
                    )
                    holder.patternsPiecesBinding.imageArrow.setImageDrawable(arrowDrawable)
                } else {
                    holder.patternsPiecesBinding.imageArrow.visibility = View.VISIBLE
                    val arrowDrawable = Utility.getDrawableFromString(
                        viewGroup!!.context,
                        "ic_arrow_vertical"
                    )
                    holder.patternsPiecesBinding.imageArrow.setImageDrawable(arrowDrawable)
                }
            }
            if (patternPieces[position].isCompleted) {
                holder.patternsPiecesBinding.imageArrow?.setColorFilter(
                    ContextCompat.getColor(
                        viewGroup!!.context,
                        R.color.gray
                    ), PorterDuff.Mode.SRC_ATOP
                )
                holder.patternsPiecesBinding.imageView?.setColorFilter(
                    ContextCompat.getColor(
                        viewGroup!!.context,
                        R.color.gray
                    ), PorterDuff.Mode.SRC_ATOP
                )
                holder.patternsPiecesBinding.imageArrow?.invalidate()
                holder.patternsPiecesBinding.imageView?.invalidate()
                holder.patternsPiecesBinding.imageArrow?.alpha = 0.5F
                holder.patternsPiecesBinding.imageView.alpha = 0.5F
            } else {
                holder.patternsPiecesBinding.imageArrow?.clearColorFilter()
                holder.patternsPiecesBinding.imageView?.clearColorFilter()
                holder.patternsPiecesBinding.imageArrow?.invalidate()
                holder.patternsPiecesBinding.imageView?.invalidate()
                holder.patternsPiecesBinding.imageArrow.alpha = 1F
                holder.patternsPiecesBinding.imageView.alpha = 1F
            }
        }
        holder.patternsPiecesBinding.imageView.setOnLongClickListener {
            val state = DragData(
                Draggable.SELECT_TO_WORKSPACE,
                position,
                it.getWidth(),
                it.getHeight(),
                patternPieces.get(position),
                null
            )
            val shadow = View.DragShadowBuilder(it)
            ViewCompat.startDragAndDrop(it, null, shadow, state, 0)
        }

    }


    inner class PatternPieceHolder(
        val patternsPiecesBinding: PatternsPiecesItemBinding,
        viewType: Int
    ) :
        RecyclerView.ViewHolder(patternsPiecesBinding.root) {
    }
}

