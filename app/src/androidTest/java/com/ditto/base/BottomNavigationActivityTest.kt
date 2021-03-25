package com.ditto.base

import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import core.ui.BottomNavigationActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for [MainActivity] class
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class BottomNavigationActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(BottomNavigationActivity::class.java)

    @Test
    fun errorViewHiddenOnStartup() {
    }

    @Test
    fun summaryCardViewDisplayedOnStartup() {
        /*onView(withId(R.id.summary_view)).check(matches(isDisplayed()))
        onView(withId(R.id.summary_title)).check(matches(isDisplayed()))
        onView(withId(R.id.summary_title)).check(matches(withText(containsString("Summary"))))
        onView(withId(R.id.profile_name_label)).check(matches(isDisplayed()))*/
    }

    @Test
    fun knowledgeCardViewDisplayedOnStartup() {
        /*onView(withId(R.id.edit_knowledge)).check(matches(isDisplayed()))

        onView(withId(R.id.label_knowledge)).check(matches(isDisplayed()))
        onView(withId(R.id.label_knowledge)).check(matches(withText(containsString("KnowledgeResponse"))))

        onView(withId(R.id.label_skill)).check(matches(isDisplayed()))
        onView(withId(R.id.label_skill)).check(matches(withText(containsString("Skill"))))

        onView(withId(R.id.label_skill_experience)).check(matches(isDisplayed()))
        onView(withId(R.id.label_skill_experience)).check(matches(withText(containsString("Exp (Yrs)"))))

        onView(withId(R.id.recycler_view_knowledge)).check(matches(isDisplayed()))
        onView(withId(R.id.label_skill_experience)).check(matches(isDisplayed()))*/
    }

    @Test
    fun workExpCardViewDisplayedOnStartup() {
        /*onView(withId(R.id.experience)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_experience)).check(matches(isDisplayed()))
        onView(withId(R.id.experience)).check(matches(withText(containsString("Work Experience"))))
        onView(withId(R.id.recycler_view_experience)).check(matches(isDisplayed()))*/
    }

}
