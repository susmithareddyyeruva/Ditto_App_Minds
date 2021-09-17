package com.ditto.mylibrary.ui.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ditto.mylibrary.domain.model.MyFolderList
import com.ditto.mylibrary.ui.AllPatternsViewModel
import com.ditto.mylibrary.ui.MyFolderViewModel
import com.ditto.mylibrary.ui.R
import com.ditto.mylibrary.ui.adapter.MyFolderListAdapter
import com.ditto.workspace.ui.util.Utility
import com.google.android.material.snackbar.Snackbar

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

        fun getAlertDialogFolder(
            context: Context,
            list: ArrayList<MyFolderList>,
            viewmodel: AllPatternsViewModel,
            callback: Utility.CallbackDialogListener,
            alertType: core.ui.common.Utility.AlertType
        ) {
            val dpi: Float = context.resources.displayMetrics.density
            val mDialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_addfolder, null)
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setView(mDialogView)
            val alert = dialogBuilder.create()
            alert.setCancelable(false)
            alert.show()
            alert?.setView(
                mDialogView,
                ((27 * dpi).toInt()),
                ((19 * dpi).toInt()),
                ((28 * dpi).toInt()),
                ((30 * dpi).toInt())
            )
            //alert?.getWindow()?.setLayout(1200, 600);
            alert.window?.setBackgroundDrawable(null)
            val negative = mDialogView.findViewById(R.id.imageCloseDialog) as ImageView
            val rvFolder = mDialogView.findViewById(R.id.rvfolders) as RecyclerView
            rvFolder.layoutManager = LinearLayoutManager(mDialogView.context)
            val adapter = MyFolderListAdapter(mDialogView.context, list,object :MyFolderListAdapter.CreateFolderListener{
                override fun onNewFolderClicked() {
                    alert.dismiss()
                    viewmodel.onCreateFolderClick()
                }
            })
            rvFolder.adapter = adapter
            adapter.viewModel = viewmodel
            dialogBuilder
                .setCancelable(false)
            negative.setOnClickListener {
                alert.dismiss()
            }


        }
        fun createFolderAlertDialog(
            context: Context,
            title: String,
            hintName: String,
            view: View,
            viewmodel: AllPatternsViewModel,
            negativeButton: String,
            positiveButton: String,
            callback: CallbackCreateFolderDialogListener,
            alertType: core.ui.common.Utility.AlertType
        ) {
            val edittext = view.findViewById(R.id.edFolderName) as EditText
            edittext.setSelection(edittext.text.length)
            edittext.setSelection(edittext.length())
            val dpi: Float = context.resources.displayMetrics.density
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton(positiveButton, DialogInterface.OnClickListener { dialog, id ->

                    Log.d("Alert event", "save and Exit")
                })
                .setNegativeButton(negativeButton, DialogInterface.OnClickListener { dialog, id ->
                    Utility.hidekyboard(
                        context,
                        view
                    )
                    dialog.dismiss()
                    callback.onCancelClicked()
                })

            Utility.alert = dialogBuilder.create()
            Utility.alert?.setView(
                view,
                ((27 * dpi).toInt()),
                ((19 * dpi).toInt()),
                ((28 * dpi).toInt()),
                ((30 * dpi).toInt())
            )
            Utility.alert?.show()
            Utility.alert?.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
                if (edittext.text.toString().isNotEmpty()) {
                    Utility.hidekyboard(
                        context,
                        view
                    )
                    Utility.alert?.dismiss()
                    callback.onCreateClicked(edittext.text.toString())
                    viewmodel.onCreateFoldersSuccess()


                } else {
                    edittext.setError("Folder Name can't be empty")
                }
            }


        }
        fun createFolderAlertDialogForMyFolder(
            context: Context,
            title: String,
            hintName: String,
            view: View,
            viewmodel: MyFolderViewModel,
            negativeButton: String,
            positiveButton: String,
            callback: CallbackCreateFolderDialogListener,
            alertType: core.ui.common.Utility.AlertType
        ) {
            val edittext = view.findViewById(R.id.edFolderName) as EditText
            edittext.setSelection(edittext.text.length)
            edittext.setSelection(edittext.length())
            val dpi: Float = context.resources.displayMetrics.density
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton(positiveButton, DialogInterface.OnClickListener { dialog, id ->

                    Log.d("Alert event", "save and Exit")
                })
                .setNegativeButton(negativeButton, DialogInterface.OnClickListener { dialog, id ->
                    Utility.hidekyboard(
                        context,
                        view
                    )
                    dialog.dismiss()
                    callback.onCancelClicked()
                })

            Utility.alert = dialogBuilder.create()
            Utility.alert?.setView(
                view,
                ((27 * dpi).toInt()),
                ((19 * dpi).toInt()),
                ((28 * dpi).toInt()),
                ((30 * dpi).toInt())
            )
            Utility.alert?.show()
            Utility.alert?.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
                if (edittext.text.toString().isNotEmpty()) {
                    Utility.hidekyboard(
                        context,
                        view
                    )
                    Utility.alert?.dismiss()
                    if (edittext.text.toString().isNotEmpty()) {
                        callback.onCreateClicked(edittext.text.toString())
                    }
                    viewmodel.onCreateFoldersSuccess()


                } else {
                    edittext.setError("Project Name can't be empty")
                }
            }


        }

        fun renameFolderAlertDialog(
            context: Context,
            view: View,
            viewmodel: MyFolderViewModel,
            negativeButton: String,
            positiveButton: String,
            callback: CallbackCreateFolderDialogListener,
            alertType: core.ui.common.Utility.AlertType
        ) {
            val edittext = view.findViewById(R.id.edRename) as EditText
            edittext.setSelection(edittext.text.length)
            edittext.setSelection(edittext.length())
            val dpi: Float = context.resources.displayMetrics.density
            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton(positiveButton, DialogInterface.OnClickListener { dialog, id ->

                    Log.d("Alert event", "save and Exit")
                })
                .setNegativeButton(negativeButton, DialogInterface.OnClickListener { dialog, id ->
                    Utility.hidekyboard(
                        context,
                        view
                    )
                    dialog.dismiss()
                    callback.onCancelClicked()
                })

            Utility.alert = dialogBuilder.create()
            Utility.alert?.setView(
                view,
                ((27 * dpi).toInt()),
                ((19 * dpi).toInt()),
                ((28 * dpi).toInt()),
                ((30 * dpi).toInt())
            )
            Utility.alert?.show()
            Utility.alert?.getButton(DialogInterface.BUTTON_POSITIVE)?.setOnClickListener {
                if (edittext.text.toString().isNotEmpty()) {
                    Utility.hidekyboard(
                        context,
                        view
                    )
                    Utility.alert?.dismiss()
                    callback.onCreateClicked(edittext.text.toString())
                    viewmodel.onCreateFoldersSuccess()


                } else {
                    edittext.setError("Project Name can't be empty")
                }
            }


        }

    }

    interface CallbackDialogListener {
        fun onPositiveButtonClicked()
        fun onNegativeButtonClicked()
    }
    interface CallbackCreateFolderDialogListener {
        fun onCreateClicked(folderName: String)
        fun onCancelClicked()
    }


}