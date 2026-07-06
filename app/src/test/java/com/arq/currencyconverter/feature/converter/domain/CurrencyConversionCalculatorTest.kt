package com.arq.currencyconverter.feature.converter.domain

import com.arq.currencyconverter.core.currencyformatter.CurrencyFormatter
import com.arq.currencyconverter.feature.converter.domain.model.ConversionInput
import com.arq.currencyconverter.feature.converter.domain.model.TickerUiModel
import com.arq.currencyconverter.feature.converter.util.ActiveInputField
import com.arq.currencyconverter.feature.converter.util.CurrencyMode
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CurrencyConversionCalculatorTest {

    private val currencyFormatter: CurrencyFormatter = mockk()
    private lateinit var calculator: CurrencyConversionCalculator

    @Before
    fun setup() {
        calculator = CurrencyConversionCalculator(currencyFormatter)
    }

    @Test
    fun `recalculate when active input is empty returns cleared inactive field`() {
        val input = ConversionInput(
            currencyMode = CurrencyMode.BID,
            activeField = ActiveInputField.SOURCE,
            sourceAmountRaw = "",
            targetAmountRaw = "100",
            ticker = null
        )

        val result = calculator.recalculate(input)

        assertEquals("", result.sourceAmount)
        assertEquals("", result.targetAmount)
    }

    @Test
    fun `recalculate when rate is null returns cleared inactive field`() {
        every { currencyFormatter.parseAmountOrNull("10") } returns BigDecimal("10")
        val input = ConversionInput(
            currencyMode = CurrencyMode.BID,
            activeField = ActiveInputField.SOURCE,
            sourceAmountRaw = "10",
            targetAmountRaw = "",
            ticker = null
        )

        val result = calculator.recalculate(input)

        assertEquals("10", result.sourceAmount)
        assertEquals("", result.targetAmount)
    }

    @Test
    fun `recalculate BID source to target calculates correctly`() {
        val ticker = TickerUiModel(bid = BigDecimal("20.0"), ask = BigDecimal("21.0"), date = "any")
        every { currencyFormatter.parseAmountOrNull("10") } returns BigDecimal("10")
        every { currencyFormatter.formatAmountForDisplay(BigDecimal("200.0")) } returns "200.0"

        val input = ConversionInput(
            currencyMode = CurrencyMode.BID,
            activeField = ActiveInputField.SOURCE,
            sourceAmountRaw = "10",
            targetAmountRaw = "",
            ticker = ticker
        )

        val result = calculator.recalculate(input)

        assertEquals("10", result.sourceAmount)
        assertEquals("200.0", result.targetAmount)
    }

    @Test
    fun `recalculate BID target to source calculates correctly`() {
        val ticker = TickerUiModel(bid = BigDecimal("20.0"), ask = BigDecimal("21.0"), date = "any")
        every { currencyFormatter.parseAmountOrNull("200") } returns BigDecimal("200")
        // The calculator uses scale 10 for division
        val expectedSource = BigDecimal("10.0000000000")
        every { currencyFormatter.formatAmountForDisplay(expectedSource) } returns "10.0000000000"

        val input = ConversionInput(
            currencyMode = CurrencyMode.BID,
            activeField = ActiveInputField.TARGET,
            sourceAmountRaw = "",
            targetAmountRaw = "200",
            ticker = ticker
        )

        val result = calculator.recalculate(input)

        assertEquals("10.0000000000", result.sourceAmount)
        assertEquals("200", result.targetAmount)
    }

    @Test
    fun `recalculate ASK source to target divides by ask`() {
        val ticker = TickerUiModel(bid = BigDecimal("20.0"), ask = BigDecimal("25.0"), date = "any")
        val divided = BigDecimal("4.0000000000")
        every { currencyFormatter.parseAmountOrNull("100") } returns BigDecimal("100")
        every { currencyFormatter.formatAmountForDisplay(divided) } returns "4.0000000000"

        val input = ConversionInput(
            currencyMode = CurrencyMode.ASK,
            activeField = ActiveInputField.SOURCE,
            sourceAmountRaw = "100",
            targetAmountRaw = "",
            ticker = ticker
        )

        val result = calculator.recalculate(input)

        assertEquals("100", result.sourceAmount)
        assertEquals("4.0000000000", result.targetAmount)
    }

    @Test
    fun `recalculate ASK target to source multiplies by ask`() {
        val ticker = TickerUiModel(bid = BigDecimal("20.0"), ask = BigDecimal("25.0"), date = "any")
        every { currencyFormatter.parseAmountOrNull("4") } returns BigDecimal("4")
        every { currencyFormatter.formatAmountForDisplay(BigDecimal("100.0")) } returns "100.0"

        val input = ConversionInput(
            currencyMode = CurrencyMode.ASK,
            activeField = ActiveInputField.TARGET,
            sourceAmountRaw = "",
            targetAmountRaw = "4",
            ticker = ticker
        )

        val result = calculator.recalculate(input)

        assertEquals("100.0", result.sourceAmount)
        assertEquals("4", result.targetAmount)
    }

    @Test
    fun `recalculate when active input is dot clears inactive field`() {
        val input = ConversionInput(
            currencyMode = CurrencyMode.BID,
            activeField = ActiveInputField.TARGET,
            sourceAmountRaw = "20",
            targetAmountRaw = ".",
            ticker = null
        )

        val result = calculator.recalculate(input)

        assertEquals("", result.sourceAmount)
        assertEquals(".", result.targetAmount)
    }

    @Test
    fun `recalculate when parsing fails clears inactive field`() {
        every { currencyFormatter.parseAmountOrNull("invalid") } returns null
        val input = ConversionInput(
            currencyMode = CurrencyMode.BID,
            activeField = ActiveInputField.SOURCE,
            sourceAmountRaw = "invalid",
            targetAmountRaw = "99",
            ticker = null
        )

        val result = calculator.recalculate(input)

        assertEquals("invalid", result.sourceAmount)
        assertEquals("", result.targetAmount)
    }

    @Test
    fun `resolveRate returns correct rate for BID and ASK`() {
        val ticker = TickerUiModel(bid = BigDecimal("19.0"), ask = BigDecimal("20.0"), date = "any")

        assertEquals(BigDecimal("19.0"), calculator.resolveRate(ticker, CurrencyMode.BID))
        assertEquals(BigDecimal("20.0"), calculator.resolveRate(ticker, CurrencyMode.ASK))
    }

    @Test
    fun `resolveRate returns null for non positive rate`() {
        val tickerZero = TickerUiModel(bid = BigDecimal.ZERO, ask = BigDecimal("20.0"), date = "any")
        val tickerNegative = TickerUiModel(
            bid = BigDecimal("19.0"),
            ask = BigDecimal("-1.0"),
            date = "any"
        )

        assertEquals(null, calculator.resolveRate(tickerZero, CurrencyMode.BID))
        assertEquals(null, calculator.resolveRate(tickerNegative, CurrencyMode.ASK))
    }

    @Test
    fun `buildExchangeRateText returns formatted rate`() {
        val ticker = TickerUiModel(
            bid = BigDecimal("20.0"),
            ask = BigDecimal("21.0"),
            date = "2023-10-10"
        )
        every { currencyFormatter.formatAmountForRate(BigDecimal("20.0")) } returns "20.00"

        val text = calculator.buildExchangeRateText(ticker, CurrencyMode.BID, "MXN")

        assert(text.contains("1 USDc = 20.00 MXN"))
        assert(text.contains("Current rate at 2023-10-10"))
    }

    @Test
    fun `buildExchangeRateText returns dash when rate is invalid`() {
        val ticker = TickerUiModel(
            bid = BigDecimal.ZERO,
            ask = BigDecimal("21.0"),
            date = "2023-10-10"
        )

        val text = calculator.buildExchangeRateText(ticker, CurrencyMode.BID, "MXN")

        assertEquals("1 USDc = — MXN", text)
    }
}
