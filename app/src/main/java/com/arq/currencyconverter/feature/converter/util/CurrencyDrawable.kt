package com.arq.currencyconverter.feature.converter.util

import androidx.annotation.DrawableRes
import com.arq.currencyconverter.R

@DrawableRes
fun currencyDrawableRes(currencyCode: String): Int = when (currencyCode) {
    "MXN" -> R.drawable.flag_mexico
    "ARS" -> R.drawable.flag_argentina
    "BRL" -> R.drawable.flag_brazil
    "COP" -> R.drawable.flag_colombia
    "USDc" -> R.drawable.logo_usdc
    else -> R.drawable.ic_flag_default
}
