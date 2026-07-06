package com.arq.currencyconverter.feature.converter.domain.model

import com.arq.currencyconverter.feature.converter.util.ActiveInputField
import com.arq.currencyconverter.feature.converter.util.CurrencyMode

data class ConversionInput(
    val currencyMode: CurrencyMode,
    val activeField: ActiveInputField,
    val sourceAmountRaw: String,
    val targetAmountRaw: String,
    val ticker: TickerUiModel?,
    val activeRawOverride: String? = null,
    val formatBothFields: Boolean = false
)
