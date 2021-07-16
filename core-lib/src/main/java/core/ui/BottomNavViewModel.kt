package core.ui

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import core.appstate.AppState
import core.event.UiEvents
import core.lib.R
import core.ui.common.MenuModel
import core.ui.common.Utility
import java.util.*
import javax.inject.Inject

class BottomNavViewModel @Inject constructor() : BaseViewModel() {

    val visibility = ObservableBoolean(true)
    val showProgress = ObservableBoolean(false)
    val menuTitle: ObservableField<String> = ObservableField("")
    val menuDescription: ObservableField<String> = ObservableField("")
    val menuNumberOfDaysForSubscription:ObservableField<String> = ObservableField("")

    val isGuestBase: ObservableBoolean = ObservableBoolean(false)
    var userEmailBase: ObservableField<String> = ObservableField("")
    var userPhoneBase: ObservableField<String> = ObservableField("")
    var userFirstNameBase: ObservableField<String> = ObservableField("")
    var userLastNameBase: ObservableField<String> = ObservableField("")
    var headerList: MutableList<MenuModel> = ArrayList<MenuModel>()
    var childList = HashMap<MenuModel, List<MenuModel>?>()
    val isLogoutEvent: ObservableBoolean = ObservableBoolean(false)
    var subscriptionEndDateBase: ObservableField<String> = ObservableField("")

    private val uiEvents = UiEvents<Event>()
    val events = uiEvents.stream()

    init {
        prepareMenuData()
        if (AppState.getIsLogged())
            isGuestBase.set(false) else
            isGuestBase.set(true)

    }

    fun refreshMenu(context: Context?) {
        prepareMenuData()
        if (!AppState.getIsLogged()) {
            menuTitle.set(context?.getString(R.string.hi_there))
            menuDescription.set(context?.getString(R.string.sign_in_to_explore_more))

        } else {
            menuTitle.set(userFirstNameBase.get() + userLastNameBase.get())
            menuDescription.set(userEmailBase.get())
            if(subscriptionEndDateBase.get().toString().isEmpty() || subscriptionEndDateBase.get()==null){
                menuNumberOfDaysForSubscription.set("0 days")
            }else{
                val days=Utility.getTotalNumberOfDays(subscriptionEndDateBase.get())
                menuNumberOfDaysForSubscription.set("$days days")
            }

        }
    }

    private fun prepareMenuData() {
        headerList.clear()
        childList.clear()
        var menuModel = MenuModel(
            "About the app & policies",
            "ic_menu_about_app",
            null
        )
        headerList.add(menuModel)
        childList.put(menuModel, menuModel.subMenu)


        val childModelsList: MutableList<MenuModel> = ArrayList<MenuModel>()
        var childModel = MenuModel(
            "Software updates",
            "ic_menu_update",
            null
        )
        childModelsList.add(childModel)
        childModel = MenuModel(
            "Manage projector",
            "ic_menu_projector",
            null
        )
        childModelsList.add(childModel)

        childModel = MenuModel(
            "Privacy policy",
            "ic_menu_privacy",
            null
        )
        childModelsList.add(childModel)

        childModel = MenuModel(
            "Workspace settings",
            "ic_ws_settings_icon",
            null
        )
        if(AppState.getIsLogged()){
            childModelsList.add(childModel)
        }


        menuModel = MenuModel(
            "Settings",
            "ic_menu_settings",
            childModelsList
        )
        headerList.add(menuModel)
        childList.put(menuModel, childModelsList)

        menuModel = MenuModel(
            "FAQ & Glossary",
            "ic_menu_faq",
            null
        )
        headerList.add(menuModel)

        menuModel = MenuModel(
            "Customer Service",
            "ic_menu_support",
            null
        )
        headerList.add(menuModel)

        if (isGuestBase?.get() != false) {
            menuModel = MenuModel(
                "Sign in",
                "ic_menu_signup",
                null
            )
            headerList.add(menuModel)
        } else {
            menuModel = MenuModel(
                "Log Out",
                "ic_menu_logout",
                null
            )
            headerList.add(menuModel)
        }
    }

    fun logout() {
        uiEvents.post(Event.NavigateToLogin)
        isLogoutEvent.set(true)
    }
    fun sigin() {
        uiEvents.post(Event.NavigateToLogin)
    }
    sealed class Event {
        object NavigateToLogin : Event()
    }


}
