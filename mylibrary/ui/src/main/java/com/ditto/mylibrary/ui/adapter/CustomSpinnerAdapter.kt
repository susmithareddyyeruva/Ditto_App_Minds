package com.ditto.mylibrary.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ditto.mylibrary.domain.model.MannequinData
import com.ditto.mylibrary.ui.R





class CustomSpinnerAdapter(val context: Context, var dataSource: List<MannequinData>) : BaseAdapter() {
    private val ITEM_HEIGHT = ViewGroup.LayoutParams.WRAP_CONTENT
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.custom_spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
     /*   if (position === 0) {
            val layoutParams: ViewGroup.LayoutParams = view.findViewById<TextView>(R.id.textMannequinId).getLayoutParams()
            layoutParams.height = 1
            view.findViewById<TextView>(R.id.textMannequinId).setLayoutParams(layoutParams)
        } else {
            val layoutParams: ViewGroup.LayoutParams = view.findViewById<TextView>(R.id.textMannequinId).getLayoutParams()
            layoutParams.height = ITEM_HEIGHT
            view.findViewById<TextView>(R.id.textMannequinId).setLayoutParams(layoutParams)
        }*/

        vh.label.text = dataSource[position].name
        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ItemHolder(row: View?) {
        val label: TextView = row?.findViewById(R.id.textMannequinId) as TextView

    }

}