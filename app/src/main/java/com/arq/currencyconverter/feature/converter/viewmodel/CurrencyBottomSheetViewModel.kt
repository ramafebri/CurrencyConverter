package com.arq.currencyconverter.feature.converter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arq.currencyconverter.core.common.UIResult
import com.arq.currencyconverter.di.DefaultDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class CurrencyBottomSheetViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _currencySource = MutableStateFlow<UIResult<List<String>>>(UIResult.Empty)

    private val _filteredCurrencyList = MutableStateFlow<UIResult<List<String>>>(UIResult.Empty)
    val filteredCurrencyList = _filteredCurrencyList.asStateFlow()

    fun setCurrencySource(source: UIResult<List<String>>) {
        _currencySource.value = source
        filterCurrencies(rawQuery = _query.value, source = source)
    }

    fun onSearchQueryChanged(query: String) {
        if (query == _query.value) return
        _query.value = query
        filterCurrencies(rawQuery = query, source = _currencySource.value)
    }

    private fun filterCurrencies(rawQuery: String, source: UIResult<List<String>>) {
        viewModelScope.launch(defaultDispatcher) {
            val normalizedQuery = rawQuery.trim()
            val nextResult = when (source) {
                is UIResult.Success -> {
                    val filtered = if (normalizedQuery.isBlank()) {
                        source.data
                    } else {
                        source.data.filter { currency ->
                            currency.contains(normalizedQuery, ignoreCase = true)
                        }
                    }
                    UIResult.Success(filtered)
                }

                is UIResult.Error -> source
                UIResult.Empty -> UIResult.Empty
            }
            withContext(Dispatchers.Main) {
                _filteredCurrencyList.value = nextResult
            }
        }
    }
}
