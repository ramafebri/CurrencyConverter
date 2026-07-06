package com.arq.currencyconverter.navigator.sessionobserver

sealed interface SessionCheckState {
    data object Loading : SessionCheckState
    data class Resolved(val userId: Long?) : SessionCheckState
}
