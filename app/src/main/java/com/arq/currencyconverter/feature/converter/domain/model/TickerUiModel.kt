package com.arq.currencyconverter.feature.converter.domain.model

import java.math.BigDecimal

data class TickerUiModel(
    val ask: BigDecimal,
    val bid: BigDecimal,
    val date: String
)
