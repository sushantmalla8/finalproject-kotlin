package com.app.merorecipe

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.app.merorecipe.ui.DashboardActivity
import com.app.merorecipe.ui.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@LargeTest
@RunWith(JUnit4::class)
class LogoutTest {
    @get:Rule
    val newRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginToLogout() {
        onView(withId(R.id.etEmail))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("sushant"))
        closeSoftKeyboard()
        Thread.sleep(1500)

        onView(withId(R.id.etPassword))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("sushant"))
        closeSoftKeyboard()
        Thread.sleep(1500)

        onView(withId(R.id.btnLogin))
            .perform(ViewActions.click())
        closeSoftKeyboard()
        Thread.sleep(10000)

        //Used to click on Action Bar Item while testing
//        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
//        onView(withId(R.id.miUser)).perform(click())

        onView(withContentDescription("User")).perform(click())
        onView(withText("Logout")).perform(click())


    }

}