package com.ditto.base

import android.content.Intent
import android.os.SystemClock
import android.provider.Settings.Global.getString
import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ditto.R
import com.ditto.base.server.MockServer
import core.appstate.AppState
import core.ui.BottomNavigationActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for [HomeFragment] class
 */

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest:BaseTest() {

    @Test
    fun verifyLogout(){
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        login("user@email.com","password")
        SystemClock.sleep(2000)
        openDrawer(R.id.drawer_layout,Gravity.RIGHT)
        onView(withText(R.string.str_menu_logout)).check(matches((isDisplayed()))).perform(click())
        onView(withId(R.id.edittext_username)).check(matches((isDisplayed())))
        onView(withId(R.id.edittext_password)).check(matches((isDisplayed())))
    }

    @Test
    fun verifyDrawer(){
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        login("user@email.com","password")
        SystemClock.sleep(2000)

        openDrawer(R.id.drawer_layout,Gravity.RIGHT)
        onView(withId(R.id.text_name)).check(matches(withText("subcust")))
        onView(withId(R.id.text_email)).check(matches(withText("subCustomer@gmail.com")))
        onView(withId(R.id.text_phone)).check(matches(withText("8653344568")))
        onView(withId(R.id.subscription_days)).check(matches(withText("0 day left")))
        onView(withText(R.string.about_the_app_amp_policies)).check(matches((isDisplayed())))
        onView(withText(R.string.str_menu_settings)).check(matches((isDisplayed())))
        onView(withText(R.string.str_menu_faq)).check(matches((isDisplayed())))
        onView(withText(R.string.str_menu_customersupport)).check(matches((isDisplayed())))
        onView(withText(R.string.str_menu_logout)).check(matches((isDisplayed())))

        onView(withText(R.string.str_menu_settings)).check(matches((isDisplayed()))).perform(click())
        onView(withText(R.string.str_menu_softwareupdate)).check(matches((isDisplayed())))
        onView(withText(R.string.str_menu_manage_projector)).check(matches((isDisplayed())))
        onView(withText(R.string.str_menu_ws_pro_settings)).check(matches((isDisplayed())))
        onView(withText(R.string.account_info)).check(matches((isDisplayed())))

    }

    @Test
    fun verifyGuestUser(){
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        onView(withId(R.id.text_guest_preview)).check(matches((isDisplayed()))).perform(click())

        SystemClock.sleep(2000)
        openDrawer(R.id.drawer_layout,Gravity.RIGHT)
        onView(withId(R.id.text_name)).check(matches(withText(R.string.hi_there)))
        onView(withId(R.id.text_email)).check(matches(withText(R.string.sign_in_to_explore_more)))
        onView(withText(R.string.str_menu_signin)).check(matches((isDisplayed())))

        onView(withText(R.string.str_menu_settings)).check(matches((isDisplayed()))).perform(click())
        onView(withText(R.string.str_menu_softwareupdate)).check(matches((isDisplayed())))
        onView(withText(R.string.str_menu_manage_projector)).check(matches((isDisplayed())))
    }

    @Test
    fun verifyDrawerSoftwareUpdate() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        login("user@email.com","password")
        SystemClock.sleep(1000)
        openDrawer(R.id.drawer_layout,Gravity.RIGHT)

        onView(withText(R.string.str_menu_settings)).check(matches((isDisplayed()))).perform(click())
        onView(withText(R.string.str_menu_softwareupdate)).check(matches((isDisplayed()))).perform(click())
        SystemClock.sleep(500)
        onView(withText("This version of the app is no longer supported and requires an update to ensure the best possible DITTO experience"))
            .inRoot(isDialog()).check(matches(isDisplayed()))

        onView(withText("SKIP")).inRoot(isDialog()).perform(click())

    }

    @Test
    fun verifyDrawerManageProjector(){
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        login("user@email.com","password")
        SystemClock.sleep(1000)
        openDrawer(R.id.drawer_layout,Gravity.RIGHT)

        onView(withText(R.string.str_menu_settings)).check(matches((isDisplayed()))).perform(click())
        onView(withText(R.string.str_menu_manage_projector)).check(matches((isDisplayed()))).perform(click())

        SystemClock.sleep(2000)

        onView(withId(R.id.textAvailable)).check(matches(withText(R.string.available))).check(matches(isDisplayed()))

        onView(withId(R.id.btnScan)).check(matches(withText(R.string.scan))).check(matches(isDisplayed())).check(
            matches(isClickable()))
        onView(withId(R.id.textCount)).check(matches(withText("0 projectors found")))

        onView(withId(R.id.noProjAvailable)).check(matches(withText(R.string.available)))
        //noProjAvailable

    }

    @Test
    fun verifyDrawerWorkspaceSetting(){}

    @Test
    fun verifyDrawerAccountInfo(){}
}