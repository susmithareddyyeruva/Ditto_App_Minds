package core.ui

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import core.appstate.AppState
import core.lib.R
import javax.inject.Inject

class BottomNavViewModel @Inject constructor() : BaseViewModel() {

    val visibility = ObservableBoolean(true)
    val showProgress = ObservableBoolean(false)
    val menuTitle: ObservableField<String> = ObservableField("")
    val menuDescription: ObservableField<String> = ObservableField("")

    val isGuestBase: ObservableBoolean = ObservableBoolean(false)
    var userEmailBase: ObservableField<String> = ObservableField("")
    var userPhoneBase: ObservableField<String> = ObservableField("")
    var userFirstNameBase: ObservableField<String> = ObservableField("")
    var userLastNameBase: ObservableField<String> = ObservableField("")

    init {
        if (AppState.getIsLogged())
            isGuestBase.set(false) else
            isGuestBase.set(true)
    }

    fun refreshMenu(context: Context?) {
        if (!AppState.getIsLogged()) {
            menuTitle.set(context?.getString(R.string.hi_there))
            menuDescription.set(context?.getString(R.string.sign_in_to_explore_more))

        } else {
            menuTitle.set(userFirstNameBase.get() + userLastNameBase.get())
            menuDescription.set(userEmailBase.get())
        }
    }
}
