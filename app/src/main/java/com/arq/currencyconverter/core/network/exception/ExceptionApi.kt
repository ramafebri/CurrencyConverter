package com.arq.currencyconverter.core.network.exception

import java.io.IOException

class UnauthorizedException(message: String) : IOException(message)
class NotFoundException(message: String) : IOException(message)
class InternalServerException(message: String) : IOException(message)
class UnknownNetworkException(message: String, cause: Throwable? = null) :
    IOException(
        message,
        cause
    )
class NoInternetException(
    message: String = "No internet connection. Please check your network.",
    cause: Throwable? = null
) : IOException(message, cause)
