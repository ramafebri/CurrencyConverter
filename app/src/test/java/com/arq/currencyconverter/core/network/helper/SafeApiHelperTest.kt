package com.arq.currencyconverter.core.network.helper

import com.arq.currencyconverter.core.network.ApiResult
import com.arq.currencyconverter.core.network.exception.UnauthorizedException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SafeApiHelperTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun `safeApiCall returns Success when apiCall succeeds`() = runTest {
        val expectedData = "Success data"
        val result = safeApiCall(testDispatcher) { expectedData }

        assert(result is ApiResult.Success)
        assertEquals(expectedData, (result as ApiResult.Success).data)
    }

    @Test
    fun `safeApiCall returns Error when apiCall throws known exception`() = runTest {
        val message = "Unauthorized access"
        val result = safeApiCall(testDispatcher) {
            throw UnauthorizedException(message)
        }

        assert(result is ApiResult.Error)
        assertEquals(message, (result as ApiResult.Error).message)
    }

    @Test
    fun `safeApiCall returns Error when apiCall throws unexpected exception`() = runTest {
        val message = "Something went wrong"
        val result = safeApiCall(testDispatcher) {
            throw RuntimeException(message)
        }

        assert(result is ApiResult.Error)
        assertEquals(message, (result as ApiResult.Error).message)
    }
}
