package com.ditto.base

import android.view.View
import android.widget.EditText
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.ditto.R
import core.appstate.AppState
import core.ui.BottomNavigationActivity
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class BaseTest {

    lateinit var mockServer: MockWebServer

    @Rule
    @JvmField
    val mActivityTestRule = ActivityTestRule(BottomNavigationActivity::class.java, false, false)

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start(8080)
        logout()
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    fun login(userNameText: String, passwordText: String) {

        val userName = Espresso.onView(ViewMatchers.withId(R.id.edittext_username))
            .check(ViewAssertions.matches((ViewMatchers.isDisplayed())))
        userName.perform(ViewActions.replaceText(userNameText), ViewActions.closeSoftKeyboard())

        val password = Espresso.onView(ViewMatchers.withId(R.id.edittext_password))
            .check(ViewAssertions.matches((ViewMatchers.isDisplayed())))
        password.perform(ViewActions.typeText(passwordText), ViewActions.closeSoftKeyboard());

        val login = Espresso.onView(ViewMatchers.withId(R.id.button_login))
            .check(ViewAssertions.matches((ViewMatchers.isDisplayed())))
        login.perform(ViewActions.click())
    }

    fun logout() {
        if (AppState.getIsLogged()) {
            AppState.logout()
            AppState.setIsLogged(false)
        }
        if(!AppState.isShownCoachMark()){
            AppState.setShowCoachMark(true)
        }
    }

    fun withError(expected: String): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(item: View?): Boolean {
                return if (item is EditText) {
                    item.error.toString() == expected
                } else false
            }

            override fun describeTo(description: Description) {
                description.appendText("Not found error message$expected, find it!")
            }

        }
    }
}