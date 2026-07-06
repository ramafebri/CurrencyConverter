package com.arq.currencyconverter.core.network.helper

import com.arq.currencyconverter.core.network.ApiResult
import com.arq.currencyconverter.core.network.exception.InternalServerException
import com.arq.currencyconverter.core.network.exception.NoInternetException
import com.arq.currencyconverter.core.network.exception.NotFoundException
import com.arq.currencyconverter.core.network.exception.UnauthorizedException
import com.arq.currencyconverter.core.network.exception.UnknownNetworkException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ApiResult<T> = withContext(dispatcher) {
    try {
        ApiResult.Success(apiCall())
    } catch (e: UnauthorizedException) {
        ApiResult.Error(e.message.orEmpty(), e)
    } catch (e: NotFoundException) {
        ApiResult.Error(e.message.orEmpty(), e)
    } catch (e: InternalServerException) {
        ApiResult.Error(e.message.orEmpty(), e)
    } catch (e: NoInternetException) {
        ApiResult.Error(e.message.orEmpty(), e)
    } catch (e: UnknownNetworkException) {
        ApiResult.Error(e.message.orEmpty(), e)
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "An unexpected error occurred.", e)
    }
}
