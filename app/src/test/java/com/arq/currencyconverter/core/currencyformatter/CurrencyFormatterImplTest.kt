package com.arq.currencyconverter.core.currencyformatter

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class CurrencyFormatterImplTest {

    private lateinit var formatter: CurrencyFormatterImpl

    @Before
    fun setup() {
        formatter = CurrencyFormatterImpl()
    }

    @Test
    fun `sanitizeRawInput keeps only digits and one dot`() {
        assertEquals("123.45", formatter.sanitizeRawInput("123.45"))
        assertEquals("123.4567", formatter.sanitizeRawInput("123.45.67"))
        assertEquals("12345", formatter.sanitizeRawInput("abc123def45"))
        assertEquals("0.12345", formatter.sanitizeRawInput("0.123456"))
        assertEquals("10.", formatter.sanitizeRawInput("10."))
        assertEquals("", formatter.sanitizeRawInput(""))
        assertEquals(".", formatter.sanitizeRawInput("."))
    }

    @Test
    fun `parseAmountOrNull returns correct BigDecimal or null`() {
        assertEquals(BigDecimal("123.45"), formatter.parseAmountOrNull("123.45"))
        assertEquals(BigDecimal("12345.67"), formatter.parseAmountOrNull("12,345.67"))
        assertNull(formatter.parseAmountOrNull(""))
        assertNull(formatter.parseAmountOrNull("."))
        assertNull(formatter.parseAmountOrNull("invalid"))
    }

    @Test
    fun `formatAmountForDisplay formats correctly with symbol and 2 decimals`() {
        assertEquals("$12,345.67", formatter.formatAmountForDisplay(BigDecimal("12345.67")))
        assertEquals("$0.00", formatter.formatAmountForDisplay(BigDecimal("0")))
        assertEquals("$12,345.67", formatter.formatAmountForDisplay(BigDecimal("12345.666")))
        assertEquals("", formatter.formatAmountForDisplay(null))
    }

    @Test
    fun `formatAmountForRate formats correctly with up to 5 decimals and no symbol`() {
        assertEquals("12,345.6789", formatter.formatAmountForRate(BigDecimal("12345.6789")))
        assertEquals("1.23457", formatter.formatAmountForRate(BigDecimal("1.234567")))
        assertEquals("1,234.3", formatter.formatAmountForRate(BigDecimal("1234.300")))
        assertEquals("1,234.367", formatter.formatAmountForRate(BigDecimal("1234.367")))
        assertEquals("", formatter.formatAmountForRate(null))
    }

    @Test
    fun `formatStringCurrency with string input formats correctly`() {
        assertEquals("$12,345.67", formatter.formatStringCurrency("12345.67"))
        assertEquals("$12,345.67", formatter.formatStringCurrency("$12,345.67"))
        assertEquals("$10.00.", formatter.formatStringCurrency("10."))
        assertEquals("", formatter.formatStringCurrency(""))
    }
}
