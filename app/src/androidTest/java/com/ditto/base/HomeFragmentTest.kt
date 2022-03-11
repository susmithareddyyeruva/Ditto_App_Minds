package com.ditto.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.*
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ditto.R
import com.ditto.base.server.MockServer
import com.google.android.material.navigation.NavigationView
import core.ui.BottomNavigationActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
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

        onView(withIndex(withId(R.id.header_view_title), 0)).check(matches(withText(R.string.str_menu_manage_projector)))

        onView(withId(R.id.textAvailable)).check(matches(withText(R.string.available))).check(matches(isDisplayed()))

        onView(withId(R.id.btnScan)).check(matches(withText(R.string.scan))).check(matches(isDisplayed())).check(
            matches(isClickable()))
        onView(withId(R.id.textCount)).check(matches(withText("0 projectors found")))

        onView(withId(R.id.noProjAvailable)).check(matches(withText(R.string.no_projector)))

        onView(withId(R.id.btnScan)).perform(click())

        //press back
        //check refresh button
        //check skip button

    }

    @Test
    fun verifyDrawerWorkspaceSetting(){
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        login("user@email.com","password")
        SystemClock.sleep(1000)
        openDrawer(R.id.drawer_layout,Gravity.RIGHT)

        onView(withText(R.string.str_menu_settings)).check(matches((isDisplayed()))).perform(click())
        onView(withText(R.string.str_menu_ws_pro_settings)).check(matches((isDisplayed()))).perform(click())

        onView(withIndex(withId(R.id.header_view_title), 0)).check(matches(withText(R.string.str_menu_ws_pro_settings)))

        onView(withId(R.id.switch_mirroringreminder)).check(matches(isChecked()))

        onView(withId(R.id.switch_multiple_piece)).check(matches(isChecked()))
        onView(withId(R.id.switch_cutnumber)).check(matches(isChecked()))
        onView(withId(R.id.switch_splicing)).check(matches(isChecked()))
    }

    @Test
    fun verifyDrawerAccountInfo(){
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        login("user@email.com","password")
        SystemClock.sleep(1000)
        openDrawer(R.id.drawer_layout,Gravity.RIGHT)

        onView(withText(R.string.str_menu_settings)).check(matches((isDisplayed()))).perform(click())

        onView(withText(R.string.str_menu_settings)).perform(swipeUp())

        SystemClock.sleep(500)
        onView(withText(R.string.account_info)).check(matches((isDisplayed()))).perform(click())

        onView(withIndex(withId(R.id.header_view_title), 0)).check(matches(withText(R.string.account_info)))

        onView(withId(R.id.tv_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_email)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_phone)).check(matches(isDisplayed()))
        onView(withId(R.id.btnDelete)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_subscription_days)).check(matches(isDisplayed()))

        onView(withId(R.id.tv_name_value)).check(matches(isDisplayed())).check(matches(withText(": sub cust")))
        onView(withId(R.id.tv_email_value)).check(matches(isDisplayed())).check(matches(withText(": subCustomer@gmail.com")))
        onView(withId(R.id.tv_phone_value)).check(matches(isDisplayed())).check(matches(withText(": 8653344568")))
        onView(withId(R.id.tv_subscription_days_value)).check(matches(isDisplayed())).check(matches(withText(": 0 days left")))
        onView(withId(R.id.btnDelete)).perform(click())

        onView(withText("NO")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())

        onView(withId(R.id.btnDelete)).perform(click())

        onView(withText("YES")).inRoot(isDialog()).check(matches(isDisplayed())).perform(click())

    }

    @Test
    fun verifyCustomerSupport(){
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        login("user@email.com","password")
        SystemClock.sleep(1000)
        openDrawer(R.id.drawer_layout,Gravity.RIGHT)

        onView(withText(R.string.str_menu_customersupport)).check(matches((isDisplayed()))).perform(click())

        SystemClock.sleep(1000)

        onView(withIndex(withId(R.id.header_view_title), 0)).check(matches(withText("Customer Support")))
        onView(withText(R.string.str_head)).check(matches(isDisplayed()))
        onView(withText(R.string.str_email)).check(matches(isDisplayed()))
        onView(withText(R.string.str_phone)).check(matches(isDisplayed()))
        onView(withText(R.string.str_quest)).check(matches(isDisplayed()))
    }

    @Test
    fun verifyFaq(){
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        login("user@email.com","password")
        SystemClock.sleep(1000)
        openDrawer(R.id.drawer_layout,Gravity.RIGHT)

        onView(withText(R.string.str_menu_faq)).check(matches((isDisplayed()))).perform(click())

    }

    @Test
    fun homeTutorials(){
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext, BottomNavigationActivity::class.java)
        mActivityTestRule.launchActivity(intent)

        login("user@email.com","password")
        SystemClock.sleep(1000)

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        SystemClock.sleep(500)

        onView(withIndex(withId(R.id.header_view_title), 1)).check(matches(withText(R.string.tutorial_header)))

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        SystemClock.sleep(500)
        onView(withIndex(withId(R.id.header_view_title), 1)).check(matches(withText(R.string.beamsetup)))

        onView(withIndex(withContentDescription("Navigate up"),1)).perform(click())

        SystemClock.sleep(500)
        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        onView(withIndex(withId(R.id.header_view_title), 1)).check(matches(withText(R.string.calibration)))

        onView(withIndex(withContentDescription("Navigate up"),1)).perform(click())

        onView(withId(R.id.recycler_view)).perform(swipeLeft())

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        SystemClock.sleep(500)
        onView(withIndex(withId(R.id.header_view_title), 1)).check(matches(withText(R.string.toolbar_title_how_to)))

        onView(withIndex(withContentDescription("Navigate up"),1)).perform(click())

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(3, click()))

    }

    fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?>? {
        return object : TypeSafeMatcher<View?>() {
            var currentIndex = 0
            var viewObjHash = 0

            @SuppressLint("DefaultLocale")
            override fun describeTo(description: Description) {
                description.appendText(String.format("with index: %d ", index))
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View?): Boolean {
                if (matcher.matches(view) && currentIndex++ == index) {
                    viewObjHash = view.hashCode()
                }
                return view.hashCode() === viewObjHash
            }
        }
    }
}