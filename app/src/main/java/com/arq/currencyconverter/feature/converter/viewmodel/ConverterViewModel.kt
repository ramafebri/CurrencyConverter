package com.arq.currencyconverter.feature.converter.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.core.currencyformatter.CurrencyFormatter
import com.arq.currencyconverter.di.DefaultDispatcher
import com.arq.currencyconverter.feature.converter.domain.CurrencyConversionCalculator
import com.arq.currencyconverter.feature.converter.domain.model.ConversionInput
import com.arq.currencyconverter.feature.converter.domain.model.ConversionResult
import com.arq.currencyconverter.feature.converter.domain.model.TickerEmission
import com.arq.currencyconverter.feature.converter.domain.model.TickerRequest
import com.arq.currencyconverter.feature.converter.domain.model.TickerUiModel
import com.arq.currencyconverter.feature.converter.domain.repository.ConverterRepository
import com.arq.currencyconverter.feature.converter.util.ActiveInputField
import com.arq.currencyconverter.feature.converter.util.CalculationTrigger
import com.arq.currencyconverter.feature.converter.util.CurrencyMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: ConverterRepository,
    private val currencyFormatter: CurrencyFormatter,
    private val conversionCalculator: CurrencyConversionCalculator,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private var currencyMode: CurrencyMode = CurrencyMode.BID

    private val _sourceCurrency = MutableStateFlow(USDC_CURRENCY)
    val sourceCurrency = _sourceCurrency.asStateFlow()

    private val _sourceTFValue = MutableStateFlow("")
    val sourceTFValue = _sourceTFValue.asStateFlow()

    private val _targetCurrency = MutableStateFlow(DEFAULT_FOREIGN_CURRENCY)
    val targetCurrency = _targetCurrency.asStateFlow()

    private val _targetTFValue = MutableStateFlow("")
    val targetTFValue = _targetTFValue.asStateFlow()

    private val _activeInputField = MutableStateFlow(ActiveInputField.SOURCE)
    private val _tickerData = MutableStateFlow<UIResult<List<TickerUiModel>>>(UIResult.Empty)

    private val _tickerCurrencyList = MutableStateFlow<UIResult<List<String>>>(UIResult.Empty)
    val tickerCurrencyList = _tickerCurrencyList.asStateFlow()

    private val _exchangeRateText = MutableStateFlow("")
    val exchangeRateText = _exchangeRateText.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet = _showBottomSheet.asStateFlow()

    private val _isLoadingRate = MutableStateFlow(false)
    val isLoadingRate = _isLoadingRate.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private val typingCalculationTriggers = MutableSharedFlow<CalculationTrigger.Typing>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val immediateCalculationTriggers = MutableSharedFlow<CalculationTrigger.Immediate>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private var latestCalculationId = 0L

    private val isAppForeground = MutableStateFlow(true)
    private val tickerRequest = MutableStateFlow<TickerRequest?>(null)
    private var tickerRequestId = 0L

    init {
        observeTickerStream()
        observeCalculationTriggers()
        loadCurrencyList()
        observeTickers(currentForeignCurrency())
    }

    @MainThread
    fun onAppForegrounded() {
        isAppForeground.value = true
    }

    @MainThread
    fun onAppBackgrounded() {
        isAppForeground.value = false
        _isLoadingRate.value = false
    }

    @MainThread
    fun onSourceFieldFocused() {
        _activeInputField.value = ActiveInputField.SOURCE
    }

    @MainThread
    fun onTargetFieldFocused() {
        _activeInputField.value = ActiveInputField.TARGET
    }

    @MainThread
    fun onSourceAmountChange(rawInput: String) {
        _activeInputField.value = ActiveInputField.SOURCE
        val sanitized = sanitizeAndApply(rawInput, _sourceTFValue) ?: return
        scheduleRecalculation(sanitized)
    }

    @MainThread
    fun onTargetAmountChange(rawInput: String) {
        _activeInputField.value = ActiveInputField.TARGET
        val sanitized = sanitizeAndApply(rawInput, _targetTFValue) ?: return
        scheduleRecalculation(sanitized)
    }

    @MainThread
    fun onDoneClicked() {
        val requestId = ++latestCalculationId
        immediateCalculationTriggers.tryEmit(
            CalculationTrigger.Immediate(
                requestId = requestId,
                formatBothFields = true
            )
        )
    }

    @MainThread
    fun onSwapCurrencyClicked() {
        latestCalculationId++

        currencyMode = if (currencyMode == CurrencyMode.BID) CurrencyMode.ASK else CurrencyMode.BID

        val tempCurrency = _sourceCurrency.value
        _sourceCurrency.value = _targetCurrency.value
        _targetCurrency.value = tempCurrency

        val tempValue = _sourceTFValue.value
        _sourceTFValue.value = _targetTFValue.value
        _targetTFValue.value = tempValue

        // After swap USDc moves to the opposite field; keep it as the active input field.
        _activeInputField.value = when (currencyMode) {
            CurrencyMode.BID -> ActiveInputField.SOURCE
            CurrencyMode.ASK -> ActiveInputField.TARGET
        }

        applyCalculationResult(recalculate())
        updateExchangeRateText()
    }

    @MainThread
    fun onForeignCurrencyRowClicked() {
        _showBottomSheet.value = true
    }

    @MainThread
    fun onDismissBottomSheet() {
        _showBottomSheet.value = false
    }

    @MainThread
    fun onCurrencySelected(currency: String) {
        _showBottomSheet.value = false
        if (currency == currentForeignCurrency()) return
        observeTickers(currency, updateForeignCurrencyOnSuccess = true)
    }

    private fun loadCurrencyList() {
        viewModelScope.launch {
            val result = repository.getTickersCurrencies()
            _tickerCurrencyList.value = when (result) {
                is UIResult.Success -> UIResult.Success(result.data)
                else -> result
            }
        }
    }

    private fun observeTickers(currency: String, updateForeignCurrencyOnSuccess: Boolean = false) {
        val nextRequest = TickerRequest(
            requestId = ++tickerRequestId,
            currency = currency,
            updateForeignCurrencyOnSuccess = updateForeignCurrencyOnSuccess
        )
        tickerRequest.value = nextRequest
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTickerStream() {
        viewModelScope.launch {
            combine(isAppForeground, tickerRequest) { foreground, request ->
                if (!foreground) {
                    null
                } else {
                    request
                }
            }.flatMapLatest { request ->
                if (request == null) {
                    emptyFlow()
                } else {
                    createTickerFlow(request)
                }
            }.collect { emission ->
                processTickerEmission(emission)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeCalculationTriggers() {
        viewModelScope.launch(defaultDispatcher) {
            merge(
                typingCalculationTriggers.debounce(INPUT_DEBOUNCE_MS),
                immediateCalculationTriggers
            ).flatMapLatest { trigger ->
                flow {
                    if (trigger.requestId != latestCalculationId) return@flow
                    emit(trigger.requestId to performCalculation(trigger))
                }
            }.collect { (_, result) ->
                applyCalculationResult(result)
            }
        }
    }

    private fun createTickerFlow(request: TickerRequest) = flow {
        _isLoadingRate.value = true
        var isInitialLoad = true
        repository.getTickers(request.currency).collect { ticker ->
            emit(TickerEmission(request, ticker, isInitialLoad))
            if (isInitialLoad) isInitialLoad = false
        }
    }

    private suspend fun processTickerEmission(emission: TickerEmission) {
        withContext(defaultDispatcher) {
            when (val ticker = emission.result) {
                is UIResult.Success -> {
                    _tickerData.value = ticker
                    if (emission.request.updateForeignCurrencyOnSuccess && emission.isInitialLoad) {
                        updateForeignCurrency(emission.request.currency)
                    }
                    latestCalculationId++
                    applyCalculationResult(recalculate())
                    updateExchangeRateText()
                }

                is UIResult.Error -> {
                    if (emission.isInitialLoad) {
                        handleTickerError(ticker)
                    }
                }

                else -> {}
            }
            if (emission.isInitialLoad) {
                _isLoadingRate.value = false
            }
        }
    }

    private suspend fun handleTickerError(ticker: UIResult.Error) {
        _tickerData.value = ticker
        _snackbarMessage.emit(ticker.message)
    }

    private fun scheduleRecalculation(updatedActiveRawValue: String) {
        val requestId = ++latestCalculationId
        typingCalculationTriggers.tryEmit(
            CalculationTrigger.Typing(
                requestId = requestId,
                activeRawOverride = updatedActiveRawValue
            )
        )
    }

    private fun sanitizeAndApply(rawInput: String, textFieldValue: MutableStateFlow<String>): String? {
        val sanitized = currencyFormatter.sanitizeRawInput(rawInput)
        if (sanitized == textFieldValue.value && rawInput != sanitized) return null
        if (sanitized != textFieldValue.value) {
            textFieldValue.value = sanitized
        }
        return sanitized
    }

    private fun performCalculation(trigger: CalculationTrigger): ConversionResult =
        when (trigger) {
            is CalculationTrigger.Typing -> recalculate(
                activeRawOverride = trigger.activeRawOverride,
                formatBothFields = false
            )

            is CalculationTrigger.Immediate -> recalculate(
                activeRawOverride = trigger.activeRawOverride,
                formatBothFields = trigger.formatBothFields
            )
        }

    private fun recalculate(
        activeRawOverride: String? = null,
        formatBothFields: Boolean = false
    ): ConversionResult = conversionCalculator.recalculate(
        ConversionInput(
            currencyMode = currencyMode,
            activeField = _activeInputField.value,
            sourceAmountRaw = _sourceTFValue.value,
            targetAmountRaw = _targetTFValue.value,
            ticker = currentTickerOrNull(),
            activeRawOverride = activeRawOverride,
            formatBothFields = formatBothFields
        )
    )

    private fun applyCalculationResult(result: ConversionResult) {
        _sourceTFValue.value = result.sourceAmount
        _targetTFValue.value = result.targetAmount
    }

    private fun currentForeignCurrency(): String = if (currencyMode == CurrencyMode.BID) {
        _targetCurrency.value
    } else {
        _sourceCurrency.value
    }

    private fun updateForeignCurrency(currency: String) {
        if (currencyMode == CurrencyMode.BID) {
            _targetCurrency.value = currency
        } else {
            _sourceCurrency.value = currency
        }
    }

    private fun updateExchangeRateText() {
        val ticker = currentTickerOrNull() ?: return
        _exchangeRateText.value = conversionCalculator.buildExchangeRateText(
            ticker = ticker,
            mode = currencyMode,
            foreignCurrency = currentForeignCurrency()
        )
    }

    private fun currentTickerOrNull(): TickerUiModel? =
        (_tickerData.value as? UIResult.Success)?.data?.firstOrNull()

    companion object {
        const val USDC_CURRENCY = CurrencyConversionCalculator.USDC_CURRENCY
        private const val DEFAULT_FOREIGN_CURRENCY = "MXN"
        private const val INPUT_DEBOUNCE_MS = 300L
    }
}
