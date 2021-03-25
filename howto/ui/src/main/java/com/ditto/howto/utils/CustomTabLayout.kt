package com.ditto.howto.utils

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout


class CustomTabLayout : TabLayout {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isTablet(context)) {
            val tabLayout: ViewGroup = getChildAt(0) as ViewGroup
            val childCount: Int = tabLayout.getChildCount()
            if (childCount != 0) {
                val displayMetrics: DisplayMetrics = context.resources.displayMetrics
                val tabMinWidth: Int = displayMetrics.widthPixels / childCount
                for (i in 0 until childCount) {
                    tabLayout.getChildAt(i).setMinimumWidth(tabMinWidth)
                }
            }

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun isTablet(context: Context): Boolean {
        val xlarge = context.getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === 4
        val large = context.getResources()
            .getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK === Configuration.SCREENLAYOUT_SIZE_LARGE
        return xlarge || large
    }
}