package com.arq.currencyconverter.navigator

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NavigatorTest {

    private lateinit var navigator: Navigator

    @Before
    fun setup() {
        navigator = Navigator(NavKeys.Signin)
    }

    @Test
    fun `initialization sets start destination`() {
        assertEquals(1, navigator.backStack.size)
        assertEquals(NavKeys.Signin, navigator.backStack.last())
    }

    @Test
    fun `navigate adds key to backstack`() {
        navigator.navigate(NavKeys.Signup)
        assertEquals(2, navigator.backStack.size)
        assertEquals(NavKeys.Signup, navigator.backStack.last())
    }

    @Test
    fun `goBack removes last key if size is greater than 1`() {
        navigator.navigate(NavKeys.Signup)
        navigator.goBack()
        assertEquals(1, navigator.backStack.size)
        assertEquals(NavKeys.Signin, navigator.backStack.last())
    }

    @Test
    fun `goBack does nothing if size is 1`() {
        navigator.goBack()
        assertEquals(1, navigator.backStack.size)
        assertEquals(NavKeys.Signin, navigator.backStack.last())
    }

    @Test
    fun `toProfile navigates to Profile`() {
        navigator.toProfile()
        assertEquals(NavKeys.Profile, navigator.backStack.last())
    }

    @Test
    fun `toConverter navigates to Converter`() {
        navigator.toConverter()
        assertEquals(NavKeys.Converter, navigator.backStack.last())
    }

    @Test
    fun `toSignup navigates to Signup`() {
        navigator.toSignup()
        assertEquals(NavKeys.Signup, navigator.backStack.last())
    }

    @Test
    fun `toSignin clears backstack and sets Signin`() {
        navigator.navigate(NavKeys.Signup)
        navigator.navigate(NavKeys.Home)

        navigator.toSignin()

        assertEquals(1, navigator.backStack.size)
        assertEquals(NavKeys.Signin, navigator.backStack.last())
    }

    @Test
    fun `toHome clears backstack and sets Home`() {
        navigator.navigate(NavKeys.Profile)

        navigator.toHome()

        assertEquals(1, navigator.backStack.size)
        assertEquals(NavKeys.Home, navigator.backStack.last())
    }
}
