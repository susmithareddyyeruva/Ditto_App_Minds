package com.ditto.mylibrary.ui.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ditto.mylibrary.domain.model.ProdDomain
import com.ditto.mylibrary.ui.AllPatternsViewModel
import com.ditto.mylibrary.ui.R
import com.ditto.mylibrary.ui.databinding.AllPatternSinglelayoutBinding
import com.ditto.workspace.ui.util.SvgBitmapDecoder
import core.appstate.AppState
import core.binding.BindableAdapter
import core.network.NetworkUtility
import core.ui.common.Utility

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
        val data=patterns[position]
       // Utility.increaseTouch(holder.patternsItemBinding.imageAdd,10f)
        setImageFromSvgPngDrawable(patterns.get(position).prodName,patterns.get(position).image,holder.patternsItemBinding.imagePattern.context,holder.patternsItemBinding.imagePattern)

//        val res: Resources = viewGroup!!.resources
//        Glide.with(holder.patternsItemBinding.imagePattern.context)
//            .load(patterns.get(position).image)
//            .placeholder(R.drawable.ic_placeholder)
//            .into(holder.patternsItemBinding.imagePattern)

        if (AppState.getIsLogged() && NetworkUtility.isNetworkAvailable(holder.patternsItemBinding.likeImage.context)) {
            if (data.isFavourite == true) {
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


        } else { //Guest User or offline users
            holder.patternsItemBinding.likeImage.visibility = View.GONE
            holder.patternsItemBinding.imageAdd.visibility = View.GONE
        }
    }

    inner class PatternHolder(
        val patternsItemBinding: AllPatternSinglelayoutBinding,
        viewType: Int
    ) :
        RecyclerView.ViewHolder(patternsItemBinding.root)


    private fun setImageFromSvgPngDrawable(
        foldername: String?,
        imagePath: String?,
        context: Context,
        imageView: ImageView
    ) {

        var availableUri: Uri? = null
        if(!(NetworkUtility.isNetworkAvailable(context))){
            availableUri = Utility.isImageFileAvailable(imagePath,"${foldername}")
            Log.d("imageUri123", " availableUri: $availableUri")
        }
        if (imagePath?.endsWith(".svg", true)!!) {
            Glide
                .with(context)
                .load(if(NetworkUtility.isNetworkAvailable(context)) imagePath else availableUri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(com.ditto.workspace.ui.R.drawable.ic_placeholder)
                .imageDecoder(SvgBitmapDecoder(context))
                .into(imageView)

        } else if (imagePath.endsWith(".png", true)) {
            Glide
                .with(context)
                .load(if(NetworkUtility.isNetworkAvailable(context)) imagePath else availableUri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(com.ditto.workspace.ui.R.drawable.ic_placeholder)
                .into(imageView)
        } else {
            Glide
                .with(context)
                .load(if(NetworkUtility.isNetworkAvailable(context)) imagePath else availableUri)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(com.ditto.workspace.ui.R.drawable.ic_placeholder)
                .into(imageView)
        }
    }

}


