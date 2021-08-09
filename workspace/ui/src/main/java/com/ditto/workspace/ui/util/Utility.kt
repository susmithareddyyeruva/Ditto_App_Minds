package com.ditto.workspace.ui.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.ditto.workspace.domain.model.DragData
import com.ditto.workspace.ui.R
import com.google.android.material.snackbar.Snackbar
import core.ui.common.Utility

/**
 * Helper Utility class (Calendar, Date/Time related methods)
 */
class Utility {

    companion object {

        val fragmentTabs: ObservableInt = ObservableInt(0)
        val workspaceItemId: ObservableInt = ObservableInt(0)
        val onDrag: ObservableBoolean = ObservableBoolean(false)
        val isOverlappingEnabled: ObservableBoolean = ObservableBoolean(false)
        val isPopupShowing: ObservableBoolean =
            ObservableBoolean(false) // stop GC clear and app going to background on multiple click
        val isDoubleTapTextVisible: ObservableBoolean = ObservableBoolean(true)

        @JvmStatic
        val progressCount: ObservableInt = ObservableInt(0)

        val mPatternPieceList: MutableList<Int> = ArrayList()
        val dragData: ObservableField<DragData> = ObservableField()
        var alert: AlertDialog? = null
        fun getAlertDialogSaveAndExit(
            context: Context,
            title: String,
            hintName: String,
            view: View,
            negativeButton: String,
            positiveButton: String,
            callback: CallbackDialogListener,
            alertType: Utility.AlertType
        ) {
            val edittext = view.findViewById(R.id.project_name) as EditText
            edittext.setSelection(edittext.text.length)
            val checkbox = view.findViewById(R.id.complete_checkbox) as CheckBox
            edittext.setText(hintName)
            edittext.setSelection(edittext.length())
            val dpi: Float = context.resources.displayMetrics.density
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton(positiveButton, DialogInterface.OnClickListener { dialog, id ->

                    Log.d("Alert event", "save and Exit")
                })
                .setNegativeButton(negativeButton, DialogInterface.OnClickListener { dialog, id ->
                    hidekyboard(
                        context,
                        view
                    )
                    dialog.dismiss()
                    callback.onExitButtonClicked()
                })

            alert = dialogBuilder.create()
            alert?.setTitle(title)
            alert?.setView(
                view,
                ((19 * dpi).toInt()),
                ((0 * dpi).toInt()),
                ((14 * dpi).toInt()),
                ((0 * dpi).toInt())
            )
            alert?.show()
            alert?.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
                if (edittext.text.toString().isNotEmpty()) {
                    hidekyboard(
                        context,
                        view
                    )
                    alert?.dismiss()
                    callback.onSaveButtonClicked(edittext.text.toString(), checkbox.isChecked)
                } else {
                    edittext.setError("Project Name can't be empty")
                }
            }
        }

        fun changeAlertPsoition(gravity: Int, mHeight: Int) {
            if (gravity == 0) {
                val wmlp: WindowManager.LayoutParams? = alert?.window?.attributes
                alert?.window?.setGravity(Gravity.TOP)
                wmlp?.gravity = Gravity.TOP
            } else if (gravity == 1) {
                val wmlp: WindowManager.LayoutParams? = alert?.window?.attributes
                if (wmlp != null) {
                    wmlp?.x = 0 //x position
                    wmlp?.y = -(mHeight / 3)
                }


                alert?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
                wmlp?.gravity = Gravity.TOP
                alert?.window?.setGravity(Gravity.TOP)
            } else {
                val wmlp: WindowManager.LayoutParams? = alert?.getWindow()?.getAttributes()
                if (wmlp != null) {
                    wmlp?.x = 0 //x position
                    wmlp?.y = 0
                }
                alert?.window?.setGravity(Gravity.CENTER)
                wmlp?.gravity = Gravity.CENTER
            }
        }

        fun showSnackBar(message: String, view: View) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }

        fun hidekyboard(mContext: Context, mView: View) {
            val inputMethodManager =
                mContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mView?.windowToken, 0)
        }
    }

    interface CallbackDialogListener {
        fun onSaveButtonClicked(projectName: String, isCompleted: Boolean?)
        fun onExitButtonClicked()
    }

}