package com.arq.currencyconverter.feature.converter.data.service

import com.arq.currencyconverter.core.network.ApiResult
import com.arq.currencyconverter.feature.converter.data.response.TickerResponse

interface ConverterService {
    suspend fun getTickers(currencyList: String): ApiResult<List<TickerResponse>>
    suspend fun getTickersCurrencies(): ApiResult<List<String>>
}
