package com.arq.currencyconverter.feature.converter.data

import app.cash.turbine.test
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.currencyformatter.CurrencyFormatter
import com.arq.currencyconverter.core.dateformatter.DateFormatter
import com.arq.currencyconverter.core.network.ApiResult
import com.arq.currencyconverter.feature.converter.data.response.TickerResponse
import com.arq.currencyconverter.feature.converter.data.service.ConverterService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterRepositoryImplTest {

    private val currencyFormatter: CurrencyFormatter = mockk()
    private val dateFormatter: DateFormatter = mockk()
    private val service: ConverterService = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: ConverterRepositoryImpl

    @Before
    fun setup() {
        repository = ConverterRepositoryImpl(
            currencyFormatter,
            dateFormatter,
            service,
            testDispatcher
        )
    }

    @Test
    fun `getTickers emits success when service is successful`() = runTest {
        val response = listOf(TickerResponse("20.0", "19.0", "usdc_mxn", "2023-10-10T10:00:00Z"))
        coEvery { service.getTickers("MXN") } returns ApiResult.Success(response)

        every { currencyFormatter.convertStringToBigDecimal("20.0") } returns BigDecimal("20.0")
        every { currencyFormatter.convertStringToBigDecimal("19.0") } returns BigDecimal("19.0")
        every { dateFormatter.formatIsoDateTime("2023-10-10T10:00:00Z") } returns "formatted-date"

        repository.getTickers("MXN").test {
            val item = awaitItem()
            assert(item is UIResult.Success)
            val data = (item as UIResult.Success).data
            assertEquals(1, data.size)
            assertEquals(BigDecimal("20.0"), data[0].ask)
            assertEquals(BigDecimal("19.0"), data[0].bid)
            assertEquals("formatted-date", data[0].date)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTickers emits error when service fails`() = runTest {
        coEvery { service.getTickers("MXN") } returns ApiResult.Error("Network error")

        repository.getTickers("MXN").test {
            val item = awaitItem()
            assert(item is UIResult.Error)
            assertEquals("Network error", (item as UIResult.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getTickersCurrencies returns success when service is successful`() = runTest {
        val response = listOf("MXN", "BRL")
        coEvery { service.getTickersCurrencies() } returns ApiResult.Success(response)

        val result = repository.getTickersCurrencies()

        assert(result is UIResult.Success)
        assertEquals(response, (result as UIResult.Success).data)
    }

    @Test
    fun `getTickersCurrencies returns default currencies when service fails`() = runTest {
        coEvery { service.getTickersCurrencies() } returns ApiResult.Error("Error")

        val result = repository.getTickersCurrencies()

        assert(result is UIResult.Success)
        assertEquals(listOf("MXN", "ARS", "BRL", "COP"), (result as UIResult.Success).data)
    }
}
