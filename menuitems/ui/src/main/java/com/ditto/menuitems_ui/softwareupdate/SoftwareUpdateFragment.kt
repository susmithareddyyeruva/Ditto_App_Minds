package com.ditto.menuitems_ui.softwareupdate

import android.R
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ditto.menuitems_ui.databinding.FragmentSoftwareUpdateBinding
import core.ui.BaseDialogFragment
import core.ui.ViewModelDialogDelegate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign


class SoftwareUpdateFragment : BaseDialogFragment() {

    private val mViewModel: SoftwareUpdateViewModel by ViewModelDialogDelegate()
    lateinit var binding: FragmentSoftwareUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSoftwareUpdateBinding.inflate(
            inflater
        ).also {
            it.viewModel = mViewModel
            it.lifecycleOwner = this
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bottomNavViewModel.showProgress.set(true)
        setStyle(DialogFragment.STYLE_NO_TITLE,R.style.Theme_Material_Dialog_Alert)
        mViewModel.disposable += mViewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun handleEvent(event: SoftwareUpdateViewModel.Event) =
        when (event) {
            SoftwareUpdateViewModel.Event.OnVersionCheckCompleted -> {
                bottomNavViewModel.showProgress.set(false)
                showVersionPopup()
            }
        }

    private fun showVersionPopup() {

    }
}