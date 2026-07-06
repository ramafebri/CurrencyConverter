package com.arq.currencyconverter.core.common

sealed interface UIResult<out T> {
    data class Success<out T>(val data: T) : UIResult<T>
    data class Error(val message: String, val exception: Throwable? = null) : UIResult<Nothing>
    object Empty : UIResult<Nothing>
}
