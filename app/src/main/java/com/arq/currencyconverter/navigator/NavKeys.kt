package com.arq.currencyconverter.navigator

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object NavKeys {

    @Serializable
    data object Splash : NavKey

    @Serializable
    data object Home : NavKey

    @Serializable
    data object Converter : NavKey

    @Serializable
    data object Signup : NavKey

    @Serializable
    data object Signin : NavKey

    @Serializable
    data object Profile : NavKey
}
