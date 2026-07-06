package com.arq.currencyconverter.feature.converter.domain.repository

import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.feature.converter.domain.model.TickerUiModel
import kotlinx.coroutines.flow.Flow

interface ConverterRepository {
    fun getTickers(currencyList: String): Flow<UIResult<List<TickerUiModel>>>
    suspend fun getTickersCurrencies(): UIResult<List<String>>
}
