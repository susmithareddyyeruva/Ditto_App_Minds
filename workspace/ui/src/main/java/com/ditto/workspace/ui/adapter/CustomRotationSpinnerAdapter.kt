package com.ditto.workspace.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ditto.workspace.ui.R

class CustomRotationSpinnerAdapter(val context: Context, val rotationOptions: List<String>) :
    BaseAdapter() {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View
            val vh: ItemHolder
            if (convertView == null) {
                view = inflater.inflate(R.layout.custom_rotation_spinner, parent, false)
                vh = ItemHolder(view)
                view?.tag = vh
            } else {
                view = convertView
                vh = view.tag as ItemHolder
            }

            vh.label.text = rotationOptions[position]

        return view
    }

    override fun getItem(position: Int): Any? {
        return rotationOptions[position];
    }

    override fun getCount(): Int {
        return rotationOptions.size - 1;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ItemHolder(row: View?) {
        val label: TextView = row?.findViewById(R.id.textRotation) as TextView
    }

}