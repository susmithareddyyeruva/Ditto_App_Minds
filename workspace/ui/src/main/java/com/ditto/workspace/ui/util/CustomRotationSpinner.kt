package com.ditto.workspace.ui.util

import android.content.Context
import android.util.AttributeSet

class CustomRotationSpinner(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatSpinner(
        context,
        attrs
    ) {
    var listener: OnItemSelectedListener? = null

    override fun setSelection(position: Int) {
        super.setSelection(position)
        if (position == selectedItemPosition) {
            listener?.onItemSelected(this, selectedView, position, selectedItemId)
        }
    }

    override fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        this.listener = listener
    }
}
