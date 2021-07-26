package com.ditto.mylibrary.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.ditto.mylibrary.ui.databinding.SearchDialogBinding
import com.ditto.mylibrary.ui.util.setBackStackData
import kotlinx.android.synthetic.main.search_dialog.view.*


class SearchDialogFragment : DialogFragment() {

    lateinit var mDataBinding: SearchDialogBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(view)
    }


    private fun setupClickListeners(view: View) {
        view.tvCAncelDialog.setOnClickListener {
            // TODO: Do some task here
            dismiss()
        }
        view.editSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mDataBinding.editSearch.text.toString().isNotEmpty()) {
                    setBackStackData("KEY_SEARCH", mDataBinding.editSearch.text.toString(), true)
                    dismiss()
                } else
                    dismiss()
                //   targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK,  activity?.intent?.putExtras(bundle));

                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog?.getWindow()?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            dialog?.getWindow()?.setStatusBarColor(Color.parseColor("#fff"));
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(getContext()),
            R.layout.search_dialog,
            null,
            false
        )
        dialog?.let {
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setCancelable(true)

        }
        return mDataBinding.root
    }


}