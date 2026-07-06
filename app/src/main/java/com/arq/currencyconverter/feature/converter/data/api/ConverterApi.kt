package com.arq.currencyconverter.feature.converter.data.api

import com.arq.currencyconverter.core.network.NetworkConstants
import com.arq.currencyconverter.feature.converter.data.response.TickerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ConverterApi {
    @GET(NetworkConstants.TICKERS)
    suspend fun getTickers(@Query("currencies") currencyList: String): List<TickerResponse>

    @GET(NetworkConstants.TICKERS_CURRENCY)
    suspend fun getTickersCurrencies(): List<String>
}
