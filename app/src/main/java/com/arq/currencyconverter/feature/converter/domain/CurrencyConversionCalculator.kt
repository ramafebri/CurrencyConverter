package com.arq.currencyconverter.feature.converter.domain

import com.arq.currencyconverter.core.currencyformatter.CurrencyFormatter
import com.arq.currencyconverter.feature.converter.domain.model.ConversionInput
import com.arq.currencyconverter.feature.converter.domain.model.ConversionResult
import com.arq.currencyconverter.feature.converter.domain.model.TickerUiModel
import com.arq.currencyconverter.feature.converter.util.ActiveInputField
import com.arq.currencyconverter.feature.converter.util.CurrencyMode
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class CurrencyConversionCalculator @Inject constructor(
    private val currencyFormatter: CurrencyFormatter
) {

    fun recalculate(input: ConversionInput): ConversionResult {
        val rate = resolveRate(input.ticker, input.currencyMode)
        val activeRaw = input.activeRawOverride ?: when (input.activeField) {
            ActiveInputField.SOURCE -> input.sourceAmountRaw
            ActiveInputField.TARGET -> input.targetAmountRaw
        }

        if (activeRaw.isBlank() || activeRaw == ".") {
            return clearInactiveField(
                activeField = input.activeField,
                sourceAmount = input.sourceAmountRaw,
                targetAmount = input.targetAmountRaw
            )
        }

        val activeAmount =
            currencyFormatter.parseAmountOrNull(activeRaw) ?: return clearInactiveField(
                activeField = input.activeField,
                sourceAmount = input.sourceAmountRaw,
                targetAmount = input.targetAmountRaw
            )

        var sourceAmount = input.sourceAmountRaw
        var targetAmount = input.targetAmountRaw

        if (input.formatBothFields) {
            val formattedActive = currencyFormatter.formatAmountForDisplay(activeAmount)
            when (input.activeField) {
                ActiveInputField.SOURCE -> sourceAmount = formattedActive
                ActiveInputField.TARGET -> targetAmount = formattedActive
            }
        }

        if (rate == null) {
            return clearInactiveField(
                activeField = input.activeField,
                sourceAmount = sourceAmount,
                targetAmount = targetAmount
            )
        }

        val converted = convertAmount(
            activeAmount = activeAmount,
            rate = rate,
            currencyMode = input.currencyMode,
            activeField = input.activeField
        ) ?: return clearInactiveField(
            activeField = input.activeField,
            sourceAmount = sourceAmount,
            targetAmount = targetAmount
        )

        val formattedConverted = currencyFormatter.formatAmountForDisplay(converted)

        return when (input.activeField) {
            ActiveInputField.SOURCE -> ConversionResult(sourceAmount, formattedConverted)
            ActiveInputField.TARGET -> ConversionResult(formattedConverted, targetAmount)
        }
    }

    fun resolveRate(ticker: TickerUiModel?, mode: CurrencyMode): BigDecimal? {
        if (ticker == null) return null

        val rate = when (mode) {
            CurrencyMode.BID -> ticker.bid
            CurrencyMode.ASK -> ticker.ask
        }
        return rate.takeIf { it > BigDecimal.ZERO }
    }

    fun buildExchangeRateText(
        ticker: TickerUiModel,
        mode: CurrencyMode,
        foreignCurrency: String
    ): String {
        val rate = resolveRate(ticker, mode)
        val currentRateDate = "\nCurrent rate at ${ticker.date}"
        return when {
            rate == null -> "1 $USDC_CURRENCY = — $foreignCurrency"

            else -> "1 $USDC_CURRENCY = ${currencyFormatter.formatAmountForRate(
                rate
            )} $foreignCurrency $currentRateDate"
        }
    }

    /**
     * BID: source is USDc, target is foreign → forward multiply, reverse divide.
     * ASK: source is foreign, target is USDc → forward divide, reverse multiply.
     */
    private fun convertAmount(
        activeAmount: BigDecimal,
        rate: BigDecimal,
        currencyMode: CurrencyMode,
        activeField: ActiveInputField
    ): BigDecimal? {
        if (rate <= BigDecimal.ZERO) return null

        return when (currencyMode) {
            CurrencyMode.BID -> when (activeField) {
                ActiveInputField.SOURCE -> activeAmount.multiply(rate)
                ActiveInputField.TARGET -> activeAmount.divide(rate, SCALE, RoundingMode.HALF_UP)
            }

            CurrencyMode.ASK -> when (activeField) {
                ActiveInputField.SOURCE -> activeAmount.divide(rate, SCALE, RoundingMode.HALF_UP)
                ActiveInputField.TARGET -> activeAmount.multiply(rate)
            }
        }
    }

    private fun clearInactiveField(
        activeField: ActiveInputField,
        sourceAmount: String,
        targetAmount: String
    ): ConversionResult = when (activeField) {
        ActiveInputField.SOURCE -> ConversionResult(sourceAmount, "")
        ActiveInputField.TARGET -> ConversionResult("", targetAmount)
    }

    companion object {
        const val USDC_CURRENCY = "USDc"
        private const val SCALE = 10
    }
}
