package com.arq.currencyconverter.feature.converter.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arq.currencyconverter.R
import com.arq.currencyconverter.feature.converter.util.currencyDrawableRes
import com.arq.currencyconverter.feature.converter.viewmodel.ConverterViewModel

private const val MAX_AMOUNT_FRACTION_DIGITS = 5

private fun isAmountInputWithinFractionLimit(input: String): Boolean {
    val dotIndex = input.indexOf('.')
    if (dotIndex == -1) return true
    return input.length - dotIndex - 1 <= MAX_AMOUNT_FRACTION_DIGITS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    viewModel: ConverterViewModel = hiltViewModel()
) {
    val showBottomSheet by viewModel.showBottomSheet.collectAsStateWithLifecycle()
    val tickerCurrencyList by viewModel.tickerCurrencyList.collectAsStateWithLifecycle()
    val sourceCurrency by viewModel.sourceCurrency.collectAsStateWithLifecycle()
    val targetCurrency by viewModel.targetCurrency.collectAsStateWithLifecycle()
    val sourceValue by viewModel.sourceTFValue.collectAsStateWithLifecycle()
    val targetValue by viewModel.targetTFValue.collectAsStateWithLifecycle()
    val exchangeRateText by viewModel.exchangeRateText.collectAsStateWithLifecycle()
    val isLoadingRate by viewModel.isLoadingRate.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    DisposableEffect(viewModel, lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.onAppForegrounded()
                Lifecycle.Event.ON_STOP -> viewModel.onAppBackgrounded()
                else -> Unit
            }
        }
        val lifecycle = lifecycleOwner.lifecycle
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    ConverterScreenContent(
        sourceCurrency = sourceCurrency,
        targetCurrency = targetCurrency,
        sourceValue = sourceValue,
        targetValue = targetValue,
        exchangeRateText = exchangeRateText,
        isLoadingRate = isLoadingRate,
        onSourceAmountChange = viewModel::onSourceAmountChange,
        onTargetAmountChange = viewModel::onTargetAmountChange,
        onSourceFieldFocused = viewModel::onSourceFieldFocused,
        onTargetFieldFocused = viewModel::onTargetFieldFocused,
        onFieldBlurred = viewModel::onDoneClicked,
        onDoneClick = {
            viewModel.onDoneClicked()
            focusManager.clearFocus()
        },
        onSwapCurrencyClick = viewModel::onSwapCurrencyClicked,
        onForeignCurrencyRowClick = viewModel::onForeignCurrencyRowClicked,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )

    if (showBottomSheet) {
        CurrencyBottomSheet(
            tickerCurrencyList = tickerCurrencyList,
            sourceCurrency = sourceCurrency,
            targetCurrency = targetCurrency,
            onDismissRequest = viewModel::onDismissBottomSheet,
            onCurrencySelected = viewModel::onCurrencySelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConverterScreenContent(
    sourceCurrency: String,
    targetCurrency: String,
    sourceValue: String,
    targetValue: String,
    exchangeRateText: String,
    isLoadingRate: Boolean,
    onSourceAmountChange: (String) -> Unit,
    onTargetAmountChange: (String) -> Unit,
    onSourceFieldFocused: () -> Unit,
    onTargetFieldFocused: () -> Unit,
    onFieldBlurred: () -> Unit,
    onDoneClick: () -> Unit,
    onSwapCurrencyClick: () -> Unit,
    onForeignCurrencyRowClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val exchangeRateLines = exchangeRateText.split("\n")
    val primaryRateText = exchangeRateLines.firstOrNull().orEmpty()
    val secondaryRateText = exchangeRateLines.getOrNull(1).orEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 60.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 20.dp
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Exchange Calculator",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = primaryRateText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (isLoadingRate) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(16.dp)
                            .testTag("LoadingIndicator"),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (secondaryRateText.isNotBlank()) {
                Text(
                    text = secondaryRateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            CurrencyInputCard(
                currencyCode = sourceCurrency,
                amount = sourceValue,
                showCurrencyPicker = sourceCurrency != ConverterViewModel.USDC_CURRENCY,
                onAmountChange = onSourceAmountChange,
                onFieldFocused = onSourceFieldFocused,
                onFieldBlurred = onFieldBlurred,
                onDoneClick = onDoneClick,
                onCurrencyRowClick = onForeignCurrencyRowClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("SourceCurrencyCard")
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .zIndex(1f)
                    .padding(top = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                SwapCurrencyButton(onClick = onSwapCurrencyClick)
            }

            CurrencyInputCard(
                currencyCode = targetCurrency,
                amount = targetValue,
                showCurrencyPicker = targetCurrency != ConverterViewModel.USDC_CURRENCY,
                onAmountChange = onTargetAmountChange,
                onFieldFocused = onTargetFieldFocused,
                onFieldBlurred = onFieldBlurred,
                onDoneClick = onDoneClick,
                onCurrencyRowClick = onForeignCurrencyRowClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
                    .padding(top = 15.dp)
                    .testTag("TargetCurrencyCard")
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun CurrencyInputCard(
    currencyCode: String,
    amount: String,
    showCurrencyPicker: Boolean,
    onAmountChange: (String) -> Unit,
    onFieldFocused: () -> Unit,
    onFieldBlurred: () -> Unit,
    onDoneClick: () -> Unit,
    onCurrencyRowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val amountTextStyle = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.End
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (showCurrencyPicker) {
                            Modifier.clickable(onClick = onCurrencyRowClick)
                        } else {
                            Modifier
                        }
                    )
                    .testTag("CurrencySelectorRow"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(currencyDrawableRes(currencyCode)),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = currencyCode,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (showCurrencyPicker) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_down),
                        contentDescription = "Choose currency",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                BasicTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (isAmountInputWithinFractionLimit(newValue)) {
                            onAmountChange(newValue)
                        }
                    },
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                onFieldFocused()
                            } else {
                                onFieldBlurred()
                            }
                        }
                        .testTag("AmountTextField_$currencyCode"),
                    textStyle = amountTextStyle,
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onDoneClick() }
                    )
                )
            }
        }
    }
}

@Composable
private fun SwapCurrencyButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_down),
            contentDescription = "Swap currencies",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}
