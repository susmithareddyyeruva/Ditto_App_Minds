package com.ditto.menuitems_ui.updateprojector.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ditto.menuitems_ui.R
import com.ditto.menuitems_ui.updateprojector.fragment.UpdateProjectorViewModel
import core.models.Nsdservicedata
import core.ui.common.Utility

class UpdateProjectorAdapter(
    private val context: Context,
    private val mServiceList: ArrayList<Nsdservicedata>,
    private val mViewModel: UpdateProjectorViewModel,
) :
    RecyclerView.Adapter<UpdateProjectorAdapter.ViewHolder>(), Utility.CallbackDialogListener {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val status: TextView = view.findViewById(R.id.textStatus)
        val scanButton: Button = view.findViewById(R.id.btnConnect)
        val updateButton: Button = view.findViewById(R.id.btnUpdate)
        val infoButton: Button = view.findViewById(R.id.btnInfo)
        val imgVideo: ImageView = view.findViewById(R.id.imageVideo)
        val projectorName: TextView = view.findViewById(R.id.textProjectorName)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_single_updateprojector, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (mServiceList[position].isConnected) {
            viewHolder.projectorName.text = mServiceList[position].nsdServiceName
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
            viewHolder.updateButton.visibility = View.VISIBLE
            viewHolder.infoButton.visibility = View.VISIBLE

        } else {
            viewHolder.projectorName.text = mServiceList[position].nsdServiceName
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
            viewHolder.updateButton.visibility = View.GONE
            viewHolder.infoButton.visibility = View.GONE
        }
        viewHolder.scanButton.setOnClickListener(View.OnClickListener {
            mViewModel.clickedPosition.set(position)
            if (mServiceList[position].isConnected) {
                mViewModel.sendWaitingImage()
                mViewModel.disConnectToProjector(mServiceList[position].nsdSericeHostAddress,
                    mServiceList[position].nsdServicePort,
                    true)
                viewHolder.updateButton.visibility = View.VISIBLE
                viewHolder.infoButton.visibility = View.VISIBLE
            } else {
                mViewModel.connect()
                viewHolder.updateButton.visibility = View.GONE
                viewHolder.infoButton.visibility = View.GONE
                //mViewModel.connectToProjector(mServiceList[position].nsdSericeHostAddress,mServiceList[position].nsdServicePort,true)
            }

        })
        viewHolder.infoButton.setOnClickListener(View.OnClickListener {
            showAlertDialogue(
                context,
                context.getString(R.string.firmwarevesion),
                context.getString(R.string.serialno),
                context.getString(R.string.macaddress),
                context.getString(R.string.str_ok),
                this,
                Utility.AlertType.UPDATE_PROJECTOR, position
            )
        })
        viewHolder.updateButton.setOnClickListener(View.OnClickListener {
            showAlertDialogueForUpdateMessage(
                context,
                context.getString(R.string.updaterojector_update_msg),
                context.getString(R.string.str_ok),
                this,
                Utility.AlertType.UPDATE_PROJECTOR
            )
        })
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = mServiceList.size

    @SuppressLint("ResourceType", "MissingInflatedId")
    fun showAlertDialogue(
        context: Context?,
        firmwareversion: String,
        serialno: String,
        macaddress: String,
        positiveButton: String,
        callbackDialogListener: Utility.CallbackDialogListener,
        alertType: Utility.AlertType,
        position: Int,
    ) {
        if (context != null) {
            val mDialogView =
                LayoutInflater.from(context)
                    .inflate(core.lib.R.layout.alert_updateprojector_info, null)
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setView(mDialogView)
            val alert = dialogBuilder.create()
            alert.setCancelable(false)
            alert.show()
            alert.window?.setBackgroundDrawable(null)
            val firmwareVersion =
                mDialogView.findViewById(core.lib.R.id.firmwareVersion) as TextView
            firmwareVersion.text = firmwareversion + ": " + "1.0.0"
            var androidID =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
            var id = androidID
            val serialNumber = mDialogView.findViewById(core.lib.R.id.serialNumber) as TextView
            serialNumber.text = serialno + ": " + id
            val macAddress = mDialogView.findViewById(core.lib.R.id.macAddress) as TextView
            macAddress.text = macaddress + ": " + mServiceList[position].nsdMacAddress
            val positive = mDialogView.findViewById(core.lib.R.id.pos_txt) as TextView
            positive.text = positiveButton
            positive.setOnClickListener {
                alert.dismiss()
                callbackDialogListener.onPositiveButtonClicked(alertType)
            }
        }
    }

    @SuppressLint("ResourceType")
    fun showAlertDialogueForUpdateMessage(
        context: Context?,
        alertmessage: String,
        positiveButton: String,
        callbackDialogListener: Utility.CallbackDialogListener,
        alertType: Utility.AlertType,
    ) {
        if (context != null) {
            val mDialogView =
                LayoutInflater.from(context)
                    .inflate(core.lib.R.layout.alert_updateprojector_message, null)
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setView(mDialogView)
            val alert = dialogBuilder.create()
            alert.setCancelable(false)
            alert.show()
            alert.window?.setBackgroundDrawable(null)
            val message = mDialogView.findViewById(core.lib.R.id.alert_message) as TextView
            message.text = alertmessage
            val positive = mDialogView.findViewById(core.lib.R.id.pos_txt) as TextView
            positive.text = positiveButton
            positive.setOnClickListener {
                alert.dismiss()
                callbackDialogListener.onPositiveButtonClicked(alertType)
            }
        }
    }

    override fun onPositiveButtonClicked(alertType: Utility.AlertType) {

    }

    override fun onNegativeButtonClicked(alertType: Utility.AlertType) {}

    override fun onNeutralButtonClicked(alertType: Utility.AlertType) {}
}