package com.app.merorecipe

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.app.merorecipe.ui.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@LargeTest
@RunWith(JUnit4::class)
class ProfileViewTest {
    @get:Rule
    val newRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginToLogout() {
        Espresso.onView(ViewMatchers.withId(R.id.etEmail))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("ramshah"))
        Espresso.closeSoftKeyboard()
        Thread.sleep(1500)

        Espresso.onView(ViewMatchers.withId(R.id.etPassword))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("ramshah"))
        Espresso.closeSoftKeyboard()
        Thread.sleep(1500)

        Espresso.onView(ViewMatchers.withId(R.id.btnLogin))
            .perform(ViewActions.click())
        Espresso.closeSoftKeyboard()
        Thread.sleep(5000)

        onView(withId(R.id.miProfile)).perform(click())
        Thread.sleep(5000)
    }
}