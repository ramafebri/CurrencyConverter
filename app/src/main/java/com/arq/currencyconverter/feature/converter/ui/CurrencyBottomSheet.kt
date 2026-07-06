package com.arq.currencyconverter.feature.converter.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.feature.converter.util.currencyDrawableRes
import com.arq.currencyconverter.feature.converter.viewmodel.ConverterViewModel
import com.arq.currencyconverter.feature.converter.viewmodel.CurrencyBottomSheetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyBottomSheet(
    tickerCurrencyList: UIResult<List<String>>,
    sourceCurrency: String,
    targetCurrency: String,
    onDismissRequest: () -> Unit,
    onCurrencySelected: (String) -> Unit,
    viewModel: CurrencyBottomSheetViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val currencyList by viewModel.filteredCurrencyList.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(tickerCurrencyList) {
        viewModel.setCurrencySource(tickerCurrencyList)
    }

    val foreignCurrency = if (sourceCurrency == ConverterViewModel.USDC_CURRENCY) {
        targetCurrency
    } else {
        sourceCurrency
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Text(
            text = "Choose currency",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier
                .testTag("CurrencySearchField")
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search currency") },
            singleLine = true
        )
        when (val list = currencyList) {
            is UIResult.Error -> {
                Text(
                    text = list.message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            is UIResult.Success -> {
                val displayCurrencies = list.data
                LazyColumn {
                    items(displayCurrencies) { currency ->
                        CurrencyListItem(
                            currency = currency,
                            isSelected = currency == foreignCurrency,
                            onClick = { onCurrencySelected(currency) }
                        )
                    }
                }
            }

            UIResult.Empty -> {}
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun CurrencyListItem(currency: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(currencyDrawableRes(currency)),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = currency, style = MaterialTheme.typography.bodyLarge)
        }
        if (isSelected) {
            Text(
                text = "✓",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
