package com.ditto.base

import android.content.Intent
import android.os.SystemClock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ditto.R
import com.ditto.base.server.MockServer
import core.ui.BottomNavigationActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for [LoginFragment] class
 */

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest : BaseTest() {

    @Test
    fun verifyUI() {

        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)
        //email hint
        onView(withId(R.id.edittext_username)).check(matches(withHint(R.string.email)))
            .check(matches((isDisplayed())))
        //password hint
        onView(withId(R.id.edittext_password)).check(matches(withHint(R.string.password)))
            .check(matches((isDisplayed())))
        //forgot password
        onView(withId(R.id.text_forgot_password)).check(matches(withText(R.string.forgot_password)))
            .check(matches((isDisplayed())))
        //Login
        onView(withId(R.id.button_login)).check(matches(withText(R.string.login)))
            .check(matches((isDisplayed()))).check(matches((isClickable())))
        //guest preview
        onView(withId(R.id.text_guest_preview)).check(matches(withText(R.string.guest_preview)))
            .check(matches((isDisplayed()))).check(matches((isClickable())))
        //signup
        onView(withId(R.id.tv_signup)).check(matches(withText(R.string.sign_up)))
            .check(matches((isDisplayed()))).check(matches((isClickable())))

    }

    @Test
    fun emailIsEmpty() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        onView(withId(R.id.edittext_username)).perform(clearText())
        onView(withId(R.id.button_login)).perform(click())
        onView(withId(R.id.edittext_username)).check(matches(withError("Invalid Email")))
    }

    @Test
    fun passwordIsEmpty() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        onView(withId(R.id.edittext_username)).perform(
            typeText("email@email.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.edittext_password)).perform(clearText())
        onView(withId(R.id.button_login)).perform(click())
        onView(withId(R.id.edittext_password)).check(matches(withError("Invalid Password")))
    }

    @Test
    fun emailIsInvalid() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        onView(withId(R.id.edittext_username)).perform(typeText("invalid"), closeSoftKeyboard())
        onView(withId(R.id.button_login)).perform(click())
        onView(withId(R.id.edittext_username)).check(matches(withError("Invalid Email")))
    }

    @Test
    fun loginSuccessCause() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        login("user@email.com", "password")
        SystemClock.sleep(2000)

        onView(withId(R.id.text_view_header)).check(matches(withText("Welcome back,")))
        onView(withId(R.id.textView_Description)).check(matches(withText("What would you like to do today?")))

    }

    @Test
    fun loginFailureCase() {
        mockServer.dispatcher = MockServer.ResponseLoginErrorDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        login("user@email.com", "password")

        onView(withText("Error Fetching data")).check(matches(isDisplayed()));
    }

    @Test
    fun guestPreview() {
        mockServer.dispatcher = MockServer.ResponseLoginSuccessDispatcher()
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            BottomNavigationActivity::class.java
        )
        mActivityTestRule.launchActivity(intent)

        onView(withId(R.id.text_guest_preview)).check(matches((isDisplayed()))).perform(click())

        onView(withId(R.id.text_view_name)).check(matches(withText("Hi there,")))
        onView(withId(R.id.textView_Description)).check(matches(withText("What would you like to do today?")))
    }
}
