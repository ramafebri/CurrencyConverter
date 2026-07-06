package com.arq.currencyconverter.core.network

sealed interface ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>
    data class Error(val message: String, val exception: Throwable? = null) : ApiResult<Nothing>
}
