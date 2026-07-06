package com.arq.currencyconverter.feature.converter.viewmodel

import com.arq.currencyconverter.core.common.UIResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyBottomSheetViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: CurrencyBottomSheetViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CurrencyBottomSheetViewModel(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setCurrencySource with blank query keeps full list`() = runTest {
        viewModel.setCurrencySource(UIResult.Success(listOf("MXN", "BRL", "ARS")))
        advanceUntilIdle()

        assertEquals(
            listOf("MXN", "BRL", "ARS"),
            (viewModel.filteredCurrencyList.value as UIResult.Success).data
        )
    }

    @Test
    fun `onSearchQueryChanged filters case-insensitive and trims query`() = runTest {
        viewModel.setCurrencySource(UIResult.Success(listOf("MXN", "BRL", "ARS")))
        viewModel.onSearchQueryChanged("  mX  ")
        advanceUntilIdle()

        assertEquals(
            listOf("MXN"),
            (viewModel.filteredCurrencyList.value as UIResult.Success).data
        )
    }

    @Test
    fun `onSearchQueryChanged with no match returns empty success list`() = runTest {
        viewModel.setCurrencySource(UIResult.Success(listOf("MXN", "BRL", "ARS")))
        viewModel.onSearchQueryChanged("zzz")
        advanceUntilIdle()

        assertEquals(
            emptyList<String>(),
            (viewModel.filteredCurrencyList.value as UIResult.Success).data
        )
    }

    @Test
    fun `setCurrencySource propagates error state`() = runTest {
        viewModel.setCurrencySource(UIResult.Error("Network error"))
        advanceUntilIdle()

        assertEquals(
            "Network error",
            (viewModel.filteredCurrencyList.value as UIResult.Error).message
        )
    }
}
