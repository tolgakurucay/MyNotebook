package com.tolgakurucay.mynotebook.view

import android.app.Instrumentation
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.getOrAwaitValueTest
import com.tolgakurucay.mynotebook.launchFragmentInHiltContainer
import com.tolgakurucay.mynotebook.viewmodels.login.LoginFragmentViewModel
import com.tolgakurucay.mynotebook.views.login.LoginActivity
import com.tolgakurucay.mynotebook.views.login.LoginFragment
import com.tolgakurucay.mynotebook.views.login.LoginFragmentDirections
import com.tolgakurucay.mynotebook.views.login.SocialLoginActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runners.JUnit4
import org.mockito.Mockito


@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
class LoginFragmentTest {

 val TAG = "bilgi"
    private lateinit var navController : NavController

    @get:Rule
    var hiltRule = HiltAndroidRule(this)



    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val intentsTestRule = IntentsTestRule(LoginActivity::class.java)

    @Before
    fun setup() {
        hiltRule.inject()

        navController = Mockito.mock(NavController::class.java)

    }


    @Test
    fun testNavigationFromLoginToForgotPassword() {

        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(ViewMatchers.withId(R.id.textViewForgotYourPassword)).perform(click())
        Mockito.verify(navController)
            .navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())


    }

    @Test
    fun testNavigationFromLoginToSignup(){
        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(requireView(),navController)
        }
        Espresso.onView(ViewMatchers.withId(R.id.textViewRegister)).perform(click())
        Mockito.verify(navController).navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }


    //intent test example(failed)
    @Test
    fun testIntentFromLoginToSocialActivity(){

        val activityMonitor = Instrumentation.ActivityMonitor(LoginActivity::class.java.name,null,true)
        Espresso.onView(ViewMatchers.withId(R.id.imageViewGoogleSign)).perform(click())
        activityMonitor.waitForActivity()
        assertEquals(1,activityMonitor.hits)


    }

    @Test
    fun testLoginSuccessfulButtonClick() = runTest {

        var testViewModel = LoginFragmentViewModel()
        launchFragmentInHiltContainer<LoginFragment> {
            testViewModel=viewModel
        }

        Espresso.onView(ViewMatchers.withId(R.id.emailSignInInput)).perform(ViewActions.replaceText("app.mynotebook@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.passwordSignInInput)).perform(ViewActions.replaceText("mynotebook2022"))
        Espresso.onView(ViewMatchers.withId(R.id.buttonSignInNow)).perform(click())

        assertThat(testViewModel.signMessageLiveData.getOrAwaitValueTest()).isEqualTo("okay")

    }

    @Test
    fun testLoginFailButtonClick() = runTest {
        var testViewModel = LoginFragmentViewModel()
        launchFragmentInHiltContainer<LoginFragment> {
            testViewModel=viewModel
        }

        Espresso.onView(ViewMatchers.withId(R.id.emailSignInInput)).perform(ViewActions.replaceText("nomail@notmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.passwordSignInInput)).perform(ViewActions.replaceText("notext"))
        Espresso.onView(ViewMatchers.withId(R.id.buttonSignInNow)).perform(click())

        assertThat(testViewModel.signMessageLiveData.getOrAwaitValueTest()).isNotEqualTo("okay")
    }


}