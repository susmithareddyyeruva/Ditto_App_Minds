package core.ui

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import core.appstate.AppState
import core.event.UiEvents
import core.lib.R
import core.ui.common.MenuModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
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

    private fun getTotalNumberOfDays(){
       // val endDate=storageManager.getStringValue(SUBSCRIPTION_END_DATE)
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val actualDate=subscriptionEndDateBase.get()?.replace("T"," ")
//            val from = LocalDate.parse(actualDate!![0], DateTimeFormatter.ofPattern("yyyy-mm-dd"))
//            val today=LocalDate.now()
//            var period = Period.between(from, today)
//
//            println("The difference between " + from.format(DateTimeFormatter.ISO_LOCAL_DATE)
//                    + " and " + today.format(DateTimeFormatter.ISO_LOCAL_DATE) + " is "
//                    + period.getYears() + " years, " + period.getMonths() + " months and "
//                    + period.getDays() + " days")
//        } else {
//            TODO("VERSION.SDK_INT < O")
//        }

        val simpleDateFormat = SimpleDateFormat("YYYY/MM/DD hh:mm:ss")
        val currentTime = Calendar.getInstance().time
        try {
            val date1: Date = simpleDateFormat.parse(actualDate)
            val date2: Date = simpleDateFormat.parse(currentTime.toString())
            printDifference(date1, date2)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    fun printDifference(startDate: Date, endDate: Date) {
        //milliseconds
        var different = endDate.time - startDate.time
        println("startDate : $startDate")
        println("endDate : $endDate")
        println("different : $different")
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        different = different % daysInMilli
        val elapsedHours = different / hoursInMilli
        different = different % hoursInMilli
        val elapsedMinutes = different / minutesInMilli
        different = different % minutesInMilli
        val elapsedSeconds = different / secondsInMilli
        System.out.printf(
            "%d days, %d hours, %d minutes, %d seconds%n",
            elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds
        )
    }

    fun refreshMenu(context: Context?) {
        prepareMenuData()
        if (!AppState.getIsLogged()) {
            menuTitle.set(context?.getString(R.string.hi_there))
            menuDescription.set(context?.getString(R.string.sign_in_to_explore_more))

        } else {
            menuTitle.set(userFirstNameBase.get() + userLastNameBase.get())
            menuDescription.set(userEmailBase.get())
            getTotalNumberOfDays()
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
            "Workspace settings",
            "ic_ws_settings_icon",
            null
        )
        if(AppState.getIsLogged()){
            childModelsList.add(childModel)
        }

        childModel = MenuModel(
            "Privacy and settings",
            "ic_menu_privacy",
            null
        )
        childModelsList.add(childModel)

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
