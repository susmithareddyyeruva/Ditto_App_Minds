package com.ditto.instructions.ui.adapter

/**
 * Created by Vishnu A V on  03/08/2020.
 * Adapter class for loading viewpager in Beamsetup and Calibration Screen
 */
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.ditto.howto.adapter.DoubleClickListener
import com.ditto.howto_ui.R
import com.ditto.instructions.domain.model.InstructionsData
import com.ditto.instructions.ui.InstructionViewModel
import com.ditto.instructions.ui.databinding.InstructionAdapterBinding
import com.ditto.workspace.ui.PinchAndZoom
import core.binding.BindableAdapter

class InstructionAdapter(val tabPosition: Int) : PagerAdapter(),
    BindableAdapter<List<InstructionsData>> {

    lateinit var viewModel: InstructionViewModel
    private var instructiondata: List<InstructionsData> = emptyList()

    override fun setListData(listData: List<InstructionsData>) {
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
        val binding = InstructionAdapterBinding.inflate(inflater, parent, false)
        binding.viewModel = viewModel
        binding.textContentHeader.text = instructiondata[position].title
        binding.textContent.text = HtmlCompat.fromHtml(instructiondata.get(position).description, HtmlCompat.FROM_HTML_MODE_LEGACY)
        val res: Resources = parent.resources
        if (!instructiondata.get(position).imagePath.equals("")) {
            val resID: Int? = res.getIdentifier(
                instructiondata.get(position).imagePath,
                "drawable",
                parent.context.packageName
            )
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
        binding.textContent.setMovementMethod(ScrollingMovementMethod())
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
        ContextCompat.startActivity(context, intent, null)
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        parent.removeView(`object` as View)
    }

}


