package com.arq.currencyconverter.feature.converter.util

internal sealed interface CalculationTrigger {
    val requestId: Long

    data class Typing(
        override val requestId: Long,
        val activeRawOverride: String
    ) : CalculationTrigger

    data class Immediate(
        override val requestId: Long,
        val activeRawOverride: String? = null,
        val formatBothFields: Boolean = false
    ) : CalculationTrigger
}

