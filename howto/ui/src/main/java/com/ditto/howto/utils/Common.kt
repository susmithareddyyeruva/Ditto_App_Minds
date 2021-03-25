package com.ditto.howto.utils


import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Sesha on  15/08/2020.
 * Class to provide the snack bar on error popups
 */
class Common {
    companion object {
        val currentSelectedTab: ObservableInt = ObservableInt(0)
        val isShowingVideoPopup: ObservableBoolean = ObservableBoolean(false)
        val isNextTabSelected: ObservableBoolean = ObservableBoolean(false)
        fun showSnackBar(message: String, view: View) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        }

    }
}