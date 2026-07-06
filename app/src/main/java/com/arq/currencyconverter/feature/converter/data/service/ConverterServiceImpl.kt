package com.arq.currencyconverter.feature.converter.data.service

import com.arq.currencyconverter.core.network.ApiResult
import com.arq.currencyconverter.core.network.helper.safeApiCall
import com.arq.currencyconverter.di.IoDispatcher
import com.arq.currencyconverter.feature.converter.data.api.ConverterApi
import com.arq.currencyconverter.feature.converter.data.response.TickerResponse
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher

class ConverterServiceImpl @Inject constructor(
    private val api: ConverterApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ConverterService {
    override suspend fun getTickers(currencyList: String): ApiResult<List<TickerResponse>> =
        safeApiCall(dispatcher) {
            api.getTickers(currencyList)
        }

    override suspend fun getTickersCurrencies(): ApiResult<List<String>> = safeApiCall(dispatcher) {
        api.getTickersCurrencies()
    }
}
