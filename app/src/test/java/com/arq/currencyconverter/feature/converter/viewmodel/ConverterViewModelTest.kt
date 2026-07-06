package com.arq.currencyconverter.feature.converter.viewmodel

import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.currencyformatter.CurrencyFormatter
import com.arq.currencyconverter.feature.converter.domain.CurrencyConversionCalculator
import com.arq.currencyconverter.feature.converter.domain.model.ConversionResult
import com.arq.currencyconverter.feature.converter.domain.model.TickerUiModel
import com.arq.currencyconverter.feature.converter.domain.repository.ConverterRepository
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {

    private val repository: ConverterRepository = mockk()
    private val currencyFormatter: CurrencyFormatter = mockk()
    private val conversionCalculator: CurrencyConversionCalculator = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ConverterViewModel

    private fun setupMocks() {
        coEvery { repository.getTickersCurrencies() } returns UIResult.Success(listOf("MXN", "BRL"))
        every { repository.getTickers(any()) } returns flowOf(UIResult.Success(listOf(mockk(relaxed = true))))
        every { conversionCalculator.recalculate(any()) } returns ConversionResult("", "")
        every {
            conversionCalculator.buildExchangeRateText(
                any(),
                any(),
                any()
            )
        } returns "Rate Text"
    }

    private fun createViewModel(dispatcher: CoroutineDispatcher = testDispatcher): ConverterViewModel =
        ConverterViewModel(
            repository,
            currencyFormatter,
            conversionCalculator,
            dispatcher
        )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        setupMocks()
        viewModel = createViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads currencies and initial tickers`() = runTest {
        assert(viewModel.tickerCurrencyList.value is UIResult.Success)
        assertEquals(
            listOf("MXN", "BRL"),
            (viewModel.tickerCurrencyList.value as UIResult.Success).data
        )

        assertEquals("Rate Text", viewModel.exchangeRateText.value)
    }

    @Test
    fun `onSourceAmountChange updates value and triggers recalculation`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val debouncedViewModel = createViewModel(dispatcher)
        advanceUntilIdle()

        every { currencyFormatter.sanitizeRawInput("10") } returns "10"
        every { conversionCalculator.recalculate(any()) } returns ConversionResult("10", "200")

        debouncedViewModel.onSourceAmountChange("10")

        assertEquals("10", debouncedViewModel.sourceTFValue.value)
        advanceTimeBy(300)
        advanceUntilIdle()
        assertEquals("200", debouncedViewModel.targetTFValue.value)
    }

    @Test
    fun `onSourceAmountChange debounces rapid input and uses latest value`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val debouncedViewModel = createViewModel(dispatcher)
        advanceUntilIdle()

        every { currencyFormatter.sanitizeRawInput("1") } returns "1"
        every { currencyFormatter.sanitizeRawInput("10") } returns "10"
        every { currencyFormatter.sanitizeRawInput("100") } returns "100"
        every { conversionCalculator.recalculate(match { it.activeRawOverride == "100" }) } returns
            ConversionResult("100", "2000")
        every { conversionCalculator.recalculate(match { it.activeRawOverride == "1" }) } returns
            ConversionResult("1", "20")
        every { conversionCalculator.recalculate(match { it.activeRawOverride == "10" }) } returns
            ConversionResult("10", "200")

        debouncedViewModel.onSourceAmountChange("1")
        debouncedViewModel.onSourceAmountChange("10")
        debouncedViewModel.onSourceAmountChange("100")

        assertEquals("100", debouncedViewModel.sourceTFValue.value)
        advanceTimeBy(300)
        advanceUntilIdle()

        assertEquals("2000", debouncedViewModel.targetTFValue.value)
        verify(exactly = 1) { conversionCalculator.recalculate(match { it.activeRawOverride == "100" }) }
    }

    @Test
    fun `onDoneClicked formats immediately and cancels pending debounced calculation`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        val debouncedViewModel = createViewModel(dispatcher)
        advanceUntilIdle()
        clearMocks(conversionCalculator, answers = false, recordedCalls = true)

        every { currencyFormatter.sanitizeRawInput("100") } returns "100"
        every { conversionCalculator.recalculate(match { !it.formatBothFields }) } returns
            ConversionResult("100", "2000")
        every { conversionCalculator.recalculate(match { it.formatBothFields }) } returns
            ConversionResult("$100.00", "$2,000.00")

        debouncedViewModel.onSourceAmountChange("100")
        debouncedViewModel.onDoneClicked()
        advanceUntilIdle()

        assertEquals("$100.00", debouncedViewModel.sourceTFValue.value)
        assertEquals("$2,000.00", debouncedViewModel.targetTFValue.value)

        advanceTimeBy(300)
        advanceUntilIdle()

        verify(exactly = 1) { conversionCalculator.recalculate(match { it.formatBothFields }) }
        verify(exactly = 0) { conversionCalculator.recalculate(match { !it.formatBothFields }) }
    }

    @Test
    fun `onSwapCurrencyClicked swaps currencies and values`() = runTest {
        val initialSource = viewModel.sourceCurrency.value
        val initialTarget = viewModel.targetCurrency.value

        viewModel.onSwapCurrencyClicked()

        assertEquals(initialTarget, viewModel.sourceCurrency.value)
        assertEquals(initialSource, viewModel.targetCurrency.value)
    }

    @Test
    fun `onCurrencySelected updates foreign currency and fetches new tickers`() = runTest {
        every {
            repository.getTickers(
                "BRL"
            )
        } returns flowOf(UIResult.Success(listOf(mockk(relaxed = true))))

        viewModel.onCurrencySelected("BRL")

        assertEquals("BRL", viewModel.targetCurrency.value)
    }

    @Test
    fun `onCurrencySelected with same foreign currency only dismisses sheet`() = runTest {
        viewModel.onForeignCurrencyRowClicked()

        viewModel.onCurrencySelected("MXN")

        assertEquals(false, viewModel.showBottomSheet.value)
        assertEquals("MXN", viewModel.targetCurrency.value)
    }

    @Test
    fun `onCurrencySelected with USDc updates target currency and dismisses sheet`() = runTest {
        viewModel.onForeignCurrencyRowClicked()

        viewModel.onCurrencySelected(ConverterViewModel.USDC_CURRENCY)

        assertEquals(false, viewModel.showBottomSheet.value)
        assertEquals(ConverterViewModel.USDC_CURRENCY, viewModel.targetCurrency.value)
    }

    @Test
    fun `init keeps currency list from repository as-is`() = runTest {
        coEvery {
            repository.getTickersCurrencies()
        } returns UIResult.Success(listOf("USDc", "MXN", "MXN", "BRL"))

        viewModel = createViewModel()

        assertEquals(
            listOf("USDc", "MXN", "MXN", "BRL"),
            (viewModel.tickerCurrencyList.value as UIResult.Success).data
        )
    }

    @Test
    fun `onCurrencySelected with same currency does not fetch new tickers`() = runTest {
        viewModel.onCurrencySelected("MXN")

        verify(exactly = 1) { repository.getTickers("MXN") }
    }

    @Test
    fun `onForeignCurrencyRowClicked shows bottom sheet`() {
        viewModel.onForeignCurrencyRowClicked()
        assertEquals(true, viewModel.showBottomSheet.value)
    }

    @Test
    fun `onAppForegrounded starts a new ticker polling`() = runTest {
        viewModel.onAppBackgrounded()
        viewModel.onAppForegrounded()

        verify(exactly = 2) { repository.getTickers("MXN") }
    }

    @Test
    fun `onAppBackgrounded cancels active polling collector`() = runTest {
        val tickerFlow = MutableSharedFlow<UIResult<List<TickerUiModel>>>()
        every { repository.getTickers("MXN") } returns tickerFlow
        every { conversionCalculator.recalculate(any()) } returns ConversionResult("", "")

        viewModel = createViewModel(UnconfinedTestDispatcher())

        tickerFlow.emit(UIResult.Success(listOf(mockk(relaxed = true))))
        advanceUntilIdle()

        clearMocks(conversionCalculator, answers = false, recordedCalls = true)

        viewModel.onAppBackgrounded()
        tickerFlow.emit(UIResult.Success(listOf(mockk(relaxed = true))))
        advanceUntilIdle()

        verify(exactly = 0) { conversionCalculator.recalculate(any()) }
    }

    @Test
    fun `onDismissBottomSheet hides bottom sheet`() {
        viewModel.onForeignCurrencyRowClicked()
        viewModel.onDismissBottomSheet()
        assertEquals(false, viewModel.showBottomSheet.value)
    }
}
