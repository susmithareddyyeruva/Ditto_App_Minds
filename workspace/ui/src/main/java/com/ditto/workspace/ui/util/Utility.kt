package com.ditto.workspace.ui.util

import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.ditto.workspace.domain.model.DragData
import com.ditto.workspace.ui.R
import com.google.android.material.snackbar.Snackbar

/**
 * Helper Utility class (Calendar, Date/Time related methods)
 */
class Utility {

    companion object {

        val fragmentTabs: ObservableInt = ObservableInt(0)
        val onDrag: ObservableBoolean = ObservableBoolean(false)
        val isOverlappingEnabled: ObservableBoolean = ObservableBoolean(false)
        val isPopupShowing: ObservableBoolean =
            ObservableBoolean(false) // stop GC clear and app going to background on multiple click
        val isDoubleTapTextVisible: ObservableBoolean = ObservableBoolean(true)
        val isLongPressTextVisible: ObservableBoolean = ObservableBoolean(false)

        @JvmStatic
        val progressCount: ObservableInt = ObservableInt(0)

        val mPatternPieceList: MutableList<Int> = ArrayList()
        val dragData: ObservableField<DragData> = ObservableField()
        var alert: AlertDialog? = null


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


        fun getNotesDialog(
            context: Context,
            notes: String,
            callback: CustomCallbackDialogListener,
        ) {
            Log.d("Testing", ">>>>>>  getNotesDialog ")
            val dpi: Float = context.resources.displayMetrics.density
            val mDialogView =
                LayoutInflater.from(context).inflate(R.layout.ws_notes_layout, null)
            val edittext = mDialogView.findViewById(R.id.note) as EditText
            val textCounter = mDialogView.findViewById(R.id.textCount) as TextView
            val okay = mDialogView.findViewById(R.id.okayBtn) as TextView
            val clear = mDialogView.findViewById(R.id.clearAll) as TextView
            val close = mDialogView.findViewById(R.id.close) as TextView

            edittext.setSelection(edittext.text.length)
            edittext.setSelection(edittext.length())
            val mTextEditorWatcher: TextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    //This sets a textview to the current length
                   /* var text = s.toString().replace("\n","")
                    var charCount = text.length*/

                    textCounter.text =
                        String.format(context.resources.getString(R.string.note_char_limit),
                            s.length.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            edittext.addTextChangedListener(mTextEditorWatcher)
            if (!notes.isNullOrEmpty()) {
                edittext.setText(notes)
            } else {
                edittext.setText("")
            }

            val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setView(mDialogView)

            dialogBuilder
                .setCancelable(false)
            /*.setPositiveButton(positiveButton) { dialog, id ->
                edittext.removeTextChangedListener(mTextEditorWatcher)
            }
            .setNegativeButton(negativeButton) { dialog, id ->

            }*/
            val alert = dialogBuilder.create()
            alert.window?.requestFeature(Window.FEATURE_NO_TITLE)
            alert.window?.setBackgroundDrawableResource(R.drawable.note_dialog_bg)

            clear.setOnClickListener {
                edittext.setText("")

            }
            okay.setOnClickListener {
                callback.onCustomPositiveButtonClicked(edittext.text.toString())
                edittext.removeTextChangedListener(mTextEditorWatcher)
                alert.dismiss()
            }

            close.setOnClickListener {
                edittext.removeTextChangedListener(mTextEditorWatcher)
                alert.dismiss()
            }

            alert.show()

            val w = context.resources.getDimension(R.dimen.note_alert_width).toInt()
            val h = context.resources.getDimension(R.dimen.note_alert_height).toInt()
            alert?.window?.setLayout(w, LayoutParams.WRAP_CONTENT)

        }

    }

    interface CallbackDialogListener {
        fun onSaveButtonClicked(projectName: String, isCompleted: Boolean?)
        fun onExitButtonClicked()
    }

    interface CustomCallbackDialogListener {
        fun onCustomPositiveButtonClicked(notes: String)
        fun onCustomNegativeButtonClicked()
    }
}