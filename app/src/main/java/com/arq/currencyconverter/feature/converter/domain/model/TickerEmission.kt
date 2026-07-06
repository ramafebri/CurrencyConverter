package com.arq.currencyconverter.feature.converter.domain.model

import com.arq.currencyconverter.core.common.UIResult

data class TickerRequest(
    val requestId: Long,
    val currency: String,
    val updateForeignCurrencyOnSuccess: Boolean
)

data class TickerEmission(
    val request: TickerRequest,
    val result: UIResult<List<TickerUiModel>>,
    val isInitialLoad: Boolean
)
