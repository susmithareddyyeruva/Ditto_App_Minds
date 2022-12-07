package com.ditto.mylibrary.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ditto.mylibrary.domain.model.SizeDomain
import com.ditto.mylibrary.ui.R

class CustomSizeSpinnerAdapter(val context: Context, var dataSource: List<SizeDomain>) : BaseAdapter() {

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
        /*  if (position>0) {
              view.setPadding(9,9,9,9)
          }else if (position==0){
              view.setPadding(3,9,9,9)
          }*/
        vh.label.text = dataSource[position].size
        return view
    }

/*    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var  viewMain=super.getDropDownView(position, convertView, parent)
        if (convertView != null) {
            viewMain=convertView
            convertView.visibility = View.VISIBLE;
            val p = convertView!!.layoutParams
            p.height = 100 // set the height
            convertView!!.layoutParams = p
        }
        return viewMain
    }*/

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