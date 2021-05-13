package com.ditto.howto.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.ditto.howto.model.HowToData
import com.ditto.howto.ui.HowtoViewModel
import com.ditto.howto.ui.PopUpWindow
import com.ditto.howto.utils.Common
import com.ditto.howto_ui.R
import com.ditto.howto_ui.databinding.WorkSpaceFragmentBinding
import com.ditto.workspace.ui.PinchAndZoom
import core.binding.BindableAdapter

/**
 * Created by Sesha on  15/08/2020.
 * Adapter class is to assign values to the content present in each tabs
 */
class TabContentAdapter(private val mContext: Context) : PagerAdapter(),
    BindableAdapter<List<HowToData>> {

    lateinit var viewModel: HowtoViewModel
    private var pos: Int = 0
    private var tabdata: List<HowToData> = emptyList()

    override fun setListData(listData: List<HowToData>) {
        tabdata = listData
        notifyDataSetChanged()
    }

    fun setPos(pos: Int) {
        this.pos = pos
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object` as View
    }

    override fun getCount(): Int {

        return tabdata.size
    }

    override fun instantiateItem(parent: ViewGroup, position: Int): Any {

            val inflater1 = LayoutInflater.from(parent.context)
            val bindingWS = WorkSpaceFragmentBinding.inflate(inflater1, parent, false)
            bindingWS.viewModel = viewModel
            bindingWS.instructionvalues = tabdata.get(position)
            bindingWS.position = pos
            val res: Resources = parent.resources
            if (!tabdata.get(position).imagePath1.equals("")) {
                val resID: Int = res.getIdentifier(
                    tabdata.get(position).imagePath1,
                    "drawable",
                    parent.context.getPackageName()
                )
                Glide.with(parent.context)
                    .load(tabdata.get(position).imagePath1)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(bindingWS.imageStep)
                if(pos==3) {
                    bindingWS.imageStep.setOnClickListener(object : DoubleClickListener() {
                        override fun onDoubleClick(v: View) { 
                            viewModel.onDoubleClick(tabdata[position].imagePath1) 
                        }
                    })
                } else {
                    bindingWS.imageStep.setOnClickListener {
                        viewModel.onItemClick(tabdata[position].videopath1,"How To")
                    }
                }
            }
        bindingWS.textContent.text = HtmlCompat.fromHtml(
            tabdata.get(position).description1,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
            bindingWS.textContent.setMovementMethod(ScrollingMovementMethod())
            parent.addView(bindingWS.root)
            return bindingWS.root
    }

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) {
        parent.removeView(`object` as View)
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

    /**
     * [Function] Watch video click
     */
    private fun showVideoPopup(
        context: Context,
        videoPath: String?
    ) {
        val intent = Intent(context, PopUpWindow::class.java)
        intent.putExtra("filename", videoPath)
        context.startActivity(intent)
    }

}

abstract class DoubleClickListener : View.OnClickListener {
    private var lastClickTime: Long = 0
    override fun onClick(v: View) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            lastClickTime = 0
        }
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View)

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}


