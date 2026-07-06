package com.arq.currencyconverter.core.currencyformatter

import java.math.BigDecimal

interface CurrencyFormatter {
    fun convertStringToBigDecimal(value: String): BigDecimal
    fun formatStringCurrency(value: String): String
    fun formatStringCurrency(value: BigDecimal): String

    /** Keeps only digits and a single decimal point; limits fractional digits to 5. */
    fun sanitizeRawInput(input: String): String

    /** Returns null for empty, ".", or invalid input instead of throwing. */
    fun parseAmountOrNull(value: String): BigDecimal?

    /** Formats for display; returns empty string when amount is null. */
    fun formatAmountForDisplay(amount: BigDecimal?): String
    fun formatAmountForRate(amount: BigDecimal?): String
}
