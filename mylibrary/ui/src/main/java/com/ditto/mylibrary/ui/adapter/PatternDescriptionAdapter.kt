package com.ditto.mylibrary.ui.adapter


import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

import com.ditto.storage.data.model.DescriptionImages
import core.binding.BindableAdapter
import com.ditto.mylibrary.ui.databinding.PatternDescImageItemBinding

/**
 * Created by Sesha on  15/08/2020.
 * Adapter class is to assign values to the content present in each tabs
 */
class PatternDescriptionAdapter() : PagerAdapter(), BindableAdapter<List<DescriptionImages>> {

     //var viewModel: PatternDescriptionViewModel = TODO()

    private var instructiondata: List<DescriptionImages> = emptyList()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun setListData(listData: List<DescriptionImages>) {
        instructiondata = listData
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return instructiondata.size!!
    }

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {


        val inflater = LayoutInflater.from(parent.context)
        val binding = PatternDescImageItemBinding.inflate(inflater, parent, false)
        //binding.viewModel = viewModel
        //binding.instructionvalues = viewModel.data.value
        //
        val res: Resources = parent.resources
//        println("ImagefromDB${instructiondata.get(position).imagePath}")
        if (!instructiondata.get(position)?.imagePath.equals("")) {
            val resID: Int = res.getIdentifier(
                instructiondata.get(position)?.imagePath,
                "drawable",
                parent.context.packageName
            )
            val drawable: Drawable = res.getDrawable(resID)
            val bitmap = (drawable as BitmapDrawable).bitmap
            binding.imagePattern.setImageBitmap(bitmap)
        }
        //

        /*val res: Resources = viewGroup!!.resources
        println("ImagefromDB${instructiondata.get(position).imagePath}")
        if (!instructiondata.get(position).imagePath.equals("")){
            val resID: Int = res.getIdentifier(instructiondata.get(position).imagePath, "drawable", viewGroup!!.context.getPackageName())
            val drawable: Drawable = res.getDrawable(resID)
            val bitmap = (drawable as BitmapDrawable).bitmap
            binding.imagePattern.setImageBitmap(bitmap)
        }*/

        parent.addView(binding.root)

        return binding.root
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        parent.removeView(`object` as View)
    }




}


