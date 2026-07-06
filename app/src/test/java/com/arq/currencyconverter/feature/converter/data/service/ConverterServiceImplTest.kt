package com.arq.currencyconverter.feature.converter.data.service

import com.arq.currencyconverter.core.network.ApiResult
import com.arq.currencyconverter.feature.converter.data.api.ConverterApi
import com.arq.currencyconverter.feature.converter.data.response.TickerResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterServiceImplTest {

    private val api: ConverterApi = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var service: ConverterServiceImpl

    @Before
    fun setup() {
        service = ConverterServiceImpl(api, testDispatcher)
    }

    @Test
    fun `getTickers returns success when api is successful`() = runTest {
        val response = listOf(TickerResponse("20.0", "19.0", "usdc_mxn", "date"))
        coEvery { api.getTickers("MXN") } returns response

        val result = service.getTickers("MXN")

        assert(result is ApiResult.Success)
        assertEquals(response, (result as ApiResult.Success).data)
    }

    @Test
    fun `getTickers returns error when api throws exception`() = runTest {
        coEvery { api.getTickers("MXN") } throws Exception("Network error")

        val result = service.getTickers("MXN")

        assert(result is ApiResult.Error)
        assertEquals("Network error", (result as ApiResult.Error).message)
    }

    @Test
    fun `getTickersCurrencies returns success when api is successful`() = runTest {
        val response = listOf("MXN", "USD")
        coEvery { api.getTickersCurrencies() } returns response

        val result = service.getTickersCurrencies()

        assert(result is ApiResult.Success)
        assertEquals(response, (result as ApiResult.Success).data)
    }
}
