package com.arq.currencyconverter.feature.converter.data

import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.currencyformatter.CurrencyFormatter
import com.arq.currencyconverter.core.dateformatter.DateFormatter
import com.arq.currencyconverter.core.network.ApiResult
import com.arq.currencyconverter.di.DefaultDispatcher
import com.arq.currencyconverter.feature.converter.data.response.TickerResponse
import com.arq.currencyconverter.feature.converter.data.service.ConverterService
import com.arq.currencyconverter.feature.converter.domain.model.TickerUiModel
import com.arq.currencyconverter.feature.converter.domain.repository.ConverterRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.minutes

class ConverterRepositoryImpl @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
    private val dateFormatter: DateFormatter,
    private val service: ConverterService,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : ConverterRepository {
    override fun getTickers(currencyList: String): Flow<UIResult<List<TickerUiModel>>> = flow {
        while (currentCoroutineContext().isActive) {
            emit(fetchTickers(currencyList))
            delay(1.minutes)
        }
    }

    private suspend fun fetchTickers(currencyList: String): UIResult<List<TickerUiModel>> =
        when (val result = service.getTickers(currencyList)) {
            is ApiResult.Success -> UIResult.Success(mapToTickerUiModel(result.data))
            is ApiResult.Error -> UIResult.Error(result.message, result.exception)
        }

    override suspend fun getTickersCurrencies(): UIResult<List<String>> =
        when (val result = service.getTickersCurrencies()) {
            is ApiResult.Success -> UIResult.Success(result.data)
            is ApiResult.Error -> UIResult.Success(DEFAULT_TICKER_CURRENCY)
        }

    private suspend fun mapToTickerUiModel(ticker: List<TickerResponse>): List<TickerUiModel> {
        return withContext(dispatcher) {
            return@withContext ticker.map {
                TickerUiModel(
                    ask = currencyFormatter.convertStringToBigDecimal(it.ask),
                    bid = currencyFormatter.convertStringToBigDecimal(it.bid),
                    date = dateFormatter.formatIsoDateTime(it.date)
                )
            }
        }
    }

    private companion object {
        val DEFAULT_TICKER_CURRENCY = listOf("MXN", "ARS", "BRL", "COP")
    }
}
