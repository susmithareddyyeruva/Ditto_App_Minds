package com.ditto.base

import android.content.Intent
import android.os.SystemClock
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ditto.R
import com.ditto.base.server.MockServer
import com.google.android.material.tabs.TabLayout
import core.ui.BottomNavigationActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for [WorkspaceFragment] class
 */

@RunWith(AndroidJUnit4::class)
class workSpaceTest : BaseTest() {

    @Test
    fun verifyUi() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        login("user@email.com", "password")
        SystemClock.sleep(2000)

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                click()
            )
        )

        onView(withId(R.id.recycler_view_patterns)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                click()
            )
        )

        onView(withIndex(withId(R.id.header_view_title), 1))
            .check(matches(withText(R.string.pattern_details)))

        onView(withId(R.id.content_header)).check(matches(withText("Full Dress Coat")))

        onView(withId(R.id.description)).check(matches(withText("Tent below knees woven dress with shoulder princess seams with center front, straight cutaway round collar stand round neck and back round jewel neck, full length fitted sleeves")))

        onView(withId(R.id.text_instructions)).check(matches(isDisplayed())).perform(click())

        onView(withIndex(withId(R.id.header_view_title), 1))
            .check(matches(withText("Pattern Instructions")))

       // onView(withText("CANCEL")).inRoot(RootMatchers.isDialog()).perform(click())

        onView(withIndex(withContentDescription("Navigate up"),1)).perform(click())

        SystemClock.sleep(1000)
        onView(withId(R.id.text_watchvideo2)).check(matches(isDisplayed())).perform(click())

    }

    @Test
    fun VerifyWorkSpaceUI() {

        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        login("user@email.com", "password")
        SystemClock.sleep(4000)

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                click()
            )
        )
        SystemClock.sleep(4000)
        onView(withId(R.id.recycler_view_patterns)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                click()
            )
        )
        onView(withId(R.id.text_watchvideo2)).check(matches(isDisplayed())).perform(click())

        SystemClock.sleep(15000)

        // onView(withId(R.id.coach_mark_popup)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.txt_select_all), 0)).check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withIndex(withId(R.id.txt_mirror_v), 0)).check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withIndex(withId(R.id.txt_mirror_h), 0)).check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withIndex(withId(R.id.txt_clear), 0)).check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withIndex(withId(R.id.txt_recalibrate), 0)).check(matches(withText("Connect")))
            .check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.txt_tutorial), 0)).check(matches(withText("Tutorial")))
            .check(matches(isDisplayed()))
        onView(
            withIndex(
                withId(R.id.txt_instructions),
                0
            )
        ).check(matches(withText("Sewing Instructions"))).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.txt_reset), 0)).check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withIndex(withId(R.id.text_complete_pieces), 0)).check(matches(withText("0/17")))
            .check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.button_send_to_projector), 0)).check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))
        onView(withIndex(withId(R.id.button_save_and_exit), 0)).check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withIndex(withId(R.id.txt_pattern_pieces), 0)).check(matches(isDisplayed()))
            .check(matches(isClickable()))
        onView(withIndex(withId(R.id.txt_reeferance_layout), 0)).check(matches(isDisplayed()))
            .check(matches(isClickable()))

        onView(
            RecyclerViewMatcher(R.id.recycler_view_pieces)
                .atPositionOnView(0, R.id.txt_piece_name)
        )
            .check(matches(withText("#8 SIDE FRONT")))

        onView(
            RecyclerViewMatcher(R.id.recycler_view_pieces)
                .atPositionOnView(1, R.id.txt_piece_name)
        )
            .check(matches(withText("#3 SIDE BACK")))

        onView(withId(R.id.tabLayout_workspace)).perform(selectTabAtPosition(2))

      /*  SystemClock.sleep(3000)

        onView(
            RecyclerViewMatcher(R.id.recycler_view_pieces)
                .atPositionOnView(0, R.id.txt_piece_name)
        )
            .check(matches(withText("#6 COLLAR"))) */
    }

    @Test
    fun exitWorkspace() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        login("user@email.com", "password")
        SystemClock.sleep(4000)

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                click()
            )
        )
        SystemClock.sleep(4000)
        onView(withId(R.id.recycler_view_patterns)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                click()
            )
        )
        onView(withId(R.id.text_watchvideo2)).check(matches(isDisplayed())).perform(click())

        SystemClock.sleep(10000)

        onView(withIndex(withId(R.id.button_save_and_exit), 0)).check(matches(isDisplayed()))
            .perform(
                click()
            )
       /* SystemClock.sleep(500)
        onView(withIndex(withId(R.id.header_view_title), 1)).check(
            matches(
                withText(
                    mActivityTestRule.activity.getString(R.string.pattern_library_count1, 18)
                )
            )
        )*/
    }

    //@Test
    fun recalibrateWorkspace() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        login("user@email.com", "password")
        SystemClock.sleep(4000)

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                click()
            )
        )
        SystemClock.sleep(4000)
        onView(withId(R.id.recycler_view_patterns)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                click()
            )
        )
        onView(withId(R.id.text_watchvideo2)).check(matches(isDisplayed())).perform(click())

        SystemClock.sleep(3000)

        onView(withIndex(withId(R.id.txt_recalibrate), 0)).check(matches(isDisplayed())).perform(
            click()
        )
        onView(withText("Select bluetooth device"))
            .inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))

        onView(withText("SKIP")).inRoot(RootMatchers.isDialog()).perform(click())
    }

    @Test
    fun redirectToTutorial() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        login("user@email.com", "password")
        SystemClock.sleep(4000)

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                click()
            )
        )
        SystemClock.sleep(4000)
        onView(withId(R.id.recycler_view_patterns)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                click()
            )
        )
        onView(withId(R.id.text_watchvideo2)).check(matches(isDisplayed())).perform(click())

        SystemClock.sleep(3000)

        onView(withIndex(withId(R.id.txt_tutorial), 0)).check(matches(isDisplayed())).perform(
            click()
        )

        SystemClock.sleep(1000)

        onView(
            withIndex(
                withId(R.id.header_view_title),
                1
            )
        ).check(matches(withText(R.string.tutorial_header)))
    }

    @Test
    fun redirectToInstruction() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        login("user@email.com", "password")
        SystemClock.sleep(4000)

        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                click()
            )
        )
        SystemClock.sleep(4000)
        onView(withId(R.id.recycler_view_patterns)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                2,
                click()
            )
        )
        onView(withId(R.id.text_watchvideo2)).check(matches(isDisplayed())).perform(click())

        SystemClock.sleep(3000)

        onView(withIndex(withId(R.id.txt_instructions), 0)).check(matches(isDisplayed())).perform(
            click()
        )

        SystemClock.sleep(1000)

       /* onView(
            withIndex(
                withId(R.id.header_view_title),
                1
            )
        ).check(matches(withText(R.string.pattern_instructions)))*/

    }

    fun selectTabAtPosition(tabIndex: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription() = "with tab at index $tabIndex"

            override fun getConstraints() =
                allOf(isDisplayed(), isAssignableFrom(TabLayout::class.java))

            override fun perform(uiController: UiController, view: View) {
                val tabLayout = view as TabLayout
                val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(tabIndex)
                    ?: throw PerformException.Builder()
                        .withCause(Throwable("No tab at index $tabIndex"))
                        .build()

                tabAtIndex.select()
            }
        }
    }
}