package com.arq.currencyconverter.navigator

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.navigation3.runtime.NavKey

class Navigator(startDestination: NavKey) {
    val backStack: SnapshotStateList<NavKey> = mutableListOf(startDestination).toMutableStateList()

    fun navigate(key: NavKey) {
        backStack.add(key)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    fun toProfile() {
        navigate(NavKeys.Profile)
    }

    fun toConverter() {
        navigate(NavKeys.Converter)
    }

    fun toSignup() {
        navigate(NavKeys.Signup)
    }

    fun toSignin() {
        backStack.clear()
        backStack.add(NavKeys.Signin)
    }

    fun toHome() {
        backStack.clear()
        backStack.add(NavKeys.Home)
    }
}
