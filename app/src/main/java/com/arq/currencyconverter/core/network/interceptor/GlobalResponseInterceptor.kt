package com.arq.currencyconverter.core.network.interceptor

import com.arq.currencyconverter.core.network.exception.InternalServerException
import com.arq.currencyconverter.core.network.exception.NoInternetException
import com.arq.currencyconverter.core.network.exception.NotFoundException
import com.arq.currencyconverter.core.network.exception.UnauthorizedException
import com.arq.currencyconverter.core.network.exception.UnknownNetworkException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import okhttp3.Interceptor
import okhttp3.Response

class GlobalResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = try {
            chain.proceed(request)
        } catch (e: UnknownHostException) {
            throw NoInternetException(cause = e)
        } catch (e: SocketTimeoutException) {
            throw UnknownNetworkException("Connection timed out. Please try again.", e)
        } catch (e: IOException) {
            throw UnknownNetworkException("Network error: ${e.message}", e)
        }

        if (response.isSuccessful) {
            return response
        }

        val code = response.code

        // close the response if we don't return it
        response.close()

        when (code) {
            401 -> throw UnauthorizedException("Session expired. Please log in again.")
            404 -> throw NotFoundException("The requested resource was not found.")
            in 500..599 -> throw InternalServerException("Server error. Please try again later.")
            else -> throw UnknownNetworkException("Network error, please check your connection")
        }
    }
}
