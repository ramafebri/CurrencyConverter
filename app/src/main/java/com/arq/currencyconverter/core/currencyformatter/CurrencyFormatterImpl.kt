package com.arq.currencyconverter.core.currencyformatter

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

internal class CurrencyFormatterImpl : CurrencyFormatter {

    override fun sanitizeRawInput(input: String): String =
        limitFractionDigits(filterToAllowedCharacters(input))

    private fun filterToAllowedCharacters(input: String): String {
        return buildString {
            var decimalSeen = false
            for (char in input) {
                when {
                    char.isDigit() -> append(char)

                    char == '.' && !decimalSeen -> {
                        append('.')
                        decimalSeen = true
                    }
                }
            }
        }
    }

    private fun limitFractionDigits(filtered: String): String {
        if (filtered.isEmpty() || filtered == ".") return filtered

        val dotIndex = filtered.indexOf('.')
        if (dotIndex == -1) return filtered

        val integerPart = filtered.substring(0, dotIndex)
        val fractionalPart = filtered.substring(dotIndex + 1).take(MAX_FRACTION_DIGITS)
        return if (filtered.endsWith('.') && fractionalPart.isEmpty()) {
            "$integerPart."
        } else {
            "$integerPart.$fractionalPart"
        }
    }

    override fun parseAmountOrNull(value: String): BigDecimal? {
        if (value.isBlank() || value == ".") return null
        return try {
            val normalized = normalizeNumericString(value)
            if (normalized.isBlank()) return null
            BigDecimal(normalized)
        } catch (_: NumberFormatException) {
            null
        }
    }

    override fun convertStringToBigDecimal(value: String): BigDecimal =
        parseAmountOrNull(value) ?: BigDecimal.ZERO

    override fun formatStringCurrency(value: String): String {
        val sanitized = sanitizeRawInput(stripCurrencySymbol(value).replace(",", ""))
        if (sanitized.isBlank() || sanitized == ".") return sanitized
        val parsed = parseAmountOrNull(sanitized) ?: return sanitized
        if (sanitized.endsWith('.')) {
            return formatString(parsed) + "."
        }
        return formatString(parsed)
    }

    override fun formatStringCurrency(value: BigDecimal): String = formatAmountForDisplay(value)

    override fun formatAmountForDisplay(amount: BigDecimal?): String {
        if (amount == null) return ""
        return formatString(amount.setScale(MAX_FRACTION_DIGITS, RoundingMode.HALF_UP))
    }

    override fun formatAmountForRate(amount: BigDecimal?): String {
        if (amount == null) return ""
        return formatString(
            amount.setScale(MAX_FRACTION_DIGITS, RoundingMode.HALF_UP),
            RATE_CURRENCY_PATTERN,
            includeCurrencySymbol = false
        )
    }

    private fun formatString(
        amount: BigDecimal,
        pattern: String = DEFAULT_CURRENCY_PATTERN,
        includeCurrencySymbol: Boolean = true
    ): String {
        val localeUs = Locale.US
        val symbolUs = DecimalFormatSymbols(localeUs)
        val formatter = DecimalFormat(pattern, symbolUs)
        val formatted = formatter.format(amount)
        return if (includeCurrencySymbol) CURRENCY_SYMBOL + formatted else formatted
    }

    private fun stripCurrencySymbol(value: String): String = value.replace(CURRENCY_SYMBOL, "")

    private fun normalizeNumericString(value: String): String {
        val trimmed = stripCurrencySymbol(value).trim().trimEnd('.')
        return when {
            trimmed.contains(',') && trimmed.contains('.') -> trimmed.replace(",", "")
            trimmed.contains(',') -> trimmed.replace(",", ".")
            else -> trimmed.replace(",", "")
        }
    }

    private companion object {
        const val CURRENCY_SYMBOL = "$"
        const val MAX_FRACTION_DIGITS = 5
        const val DEFAULT_CURRENCY_PATTERN = "#,##0.00"
        const val RATE_CURRENCY_PATTERN = "#,##0.#####"
    }
}
