package com.ditto.instructions.ui.adapter

/**
 * Created by Vishnu A V on  03/08/2020.
 * Adapter class for loading viewpager in Beamsetup and Calibration Screen
 */
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.ditto.instructions.domain.model.InstructionModel
import com.ditto.instructions.ui.InstructionViewModel
import com.ditto.instructions.ui.R
import com.ditto.instructions.ui.databinding.InstructionCalibrationAdapterBinding
import com.ditto.workspace.ui.PinchAndZoom
import core.binding.BindableAdapter
import core.ui.common.DoubleClickListener

class InstructionCalibrationAdapter() : PagerAdapter(), BindableAdapter<List<InstructionModel>> {

    lateinit var viewModel: InstructionViewModel
    private var instructiondata: List<InstructionModel> = emptyList()

    override fun setListData(listData: List<InstructionModel>) {
        instructiondata = listData
        println("************** notifyDataSetChanged")
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object` as View
    }

    override fun getCount(): Int {

        return instructiondata.size
    }

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {

        val inflater = LayoutInflater.from(parent.context)
        val binding = InstructionCalibrationAdapterBinding.inflate(inflater, parent, false)
        binding.viewModel = viewModel
        binding.textContentHeader.text = instructiondata[position].title
        binding.textContent.text = HtmlCompat.fromHtml(instructiondata.get(position).description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        if (!instructiondata.get(position).imagePath.equals("")) {
            /*val drawable: Drawable? = resID?.let { ResourcesCompat.getDrawable(res,it,null) }
            val bitmap = (drawable as BitmapDrawable).bitmap
            binding.imageStep.setImageBitmap(bitmap)*/
            Glide.with(parent.context)
                .load(instructiondata[position].imagePath)
                .placeholder(R.drawable.ic_placeholder)
                .into(binding.imageStep)
            binding.imageStep.setOnClickListener(object : DoubleClickListener() {
                override fun onDoubleClick(v: View) {
                    viewModel.onClickPlayVideo()
                }
            })
        }

        val observer: ViewTreeObserver = binding.scrollViewContent.viewTreeObserver
        observer.addOnGlobalLayoutListener {
            val viewHeight: Int = binding.scrollViewContent.measuredHeight
            val contentHeight: Int = binding.scrollViewContent.getChildAt(0).height
            if (viewHeight - contentHeight < 0) {
                binding.scrollViewBorder.visibility = View.VISIBLE
            } else {
                binding.scrollViewBorder.visibility = View.GONE
            }
        }

        parent.addView(binding.root)
        return binding.root
    }


    fun showPinchZoomPopup(context: Context, imagePath: String?) {
        val intent = Intent(context, PinchAndZoom::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.putExtra("ImageURL", imagePath)
        intent.putExtra("isFrom", "Caliberation")
        ContextCompat.startActivity(context, intent, null)
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        parent.removeView(`object` as View)
    }

}


