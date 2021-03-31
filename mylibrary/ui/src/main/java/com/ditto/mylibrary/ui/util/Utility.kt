package com.ditto.mylibrary.ui.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.ditto.mylibrary.ui.R

/**
 * Helper Utility class (Calendar, Date/Time related methods)
 */
class Utility {

    companion object {

        fun getAlertDialogue(
            context: Context,
            title: String,
            message: String,
            negativeButton: String,
            positiveButton: String,
            callbackDialogListener: CallbackDialogListener
        ) {
            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(context)
            // set message of alert dialog
            dialogBuilder.setMessage(message)
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                // positive button text and action
                .setPositiveButton(positiveButton, DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    callbackDialogListener.onPositiveButtonClicked()
                })
                // negative button text and action
                .setNegativeButton(negativeButton, DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                    callbackDialogListener.onNegativeButtonClicked()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle(title)
            // show alert dialog
            alert.show()
        }

        fun showSnackBar(message: String, view: View) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    interface CallbackDialogListener {
        fun onPositiveButtonClicked()
        fun onNegativeButtonClicked()
    }

}