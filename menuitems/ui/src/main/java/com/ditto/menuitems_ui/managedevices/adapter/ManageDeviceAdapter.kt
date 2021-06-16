package com.ditto.menuitems_ui.managedevices.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ditto.menuitems_ui.R

class ManageDeviceAdapter(private val context: Context) :
    RecyclerView.Adapter<ManageDeviceAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val status: TextView = view.findViewById(R.id.textStatus)
        val scanButton: Button = view.findViewById(R.id.btnConnect)
        val imgVideo:ImageView=view.findViewById(R.id.imageVideo)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_single_projector, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (position == 0) {
            viewHolder.status.visibility = View.VISIBLE
            viewHolder.scanButton.background =
                ContextCompat.getDrawable(context, R.drawable.bg_disconnect)
            viewHolder.scanButton.setTextColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.white
                )
            )
            viewHolder.scanButton.setText("Disconnect")
            viewHolder.imgVideo.setImageResource(R.drawable.ic_video)
        } else {
            viewHolder.status.visibility = View.INVISIBLE
            viewHolder.scanButton.background =
                ContextCompat.getDrawable(context, R.drawable.bg_connect)
            viewHolder.scanButton.setTextColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.black
                )
            )
            viewHolder.scanButton.setText("Connect")
            viewHolder.imgVideo.setImageResource(R.drawable.ic_default_video)
        }


        // Get element from your dataset at this position and replace the
        // contents of the view with that element
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = 3

}