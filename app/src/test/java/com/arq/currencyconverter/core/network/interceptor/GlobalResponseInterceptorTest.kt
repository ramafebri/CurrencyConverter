package com.arq.currencyconverter.core.network.interceptor

import com.arq.currencyconverter.core.network.exception.InternalServerException
import com.arq.currencyconverter.core.network.exception.NotFoundException
import com.arq.currencyconverter.core.network.exception.UnauthorizedException
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GlobalResponseInterceptorTest {

    private lateinit var interceptor: GlobalResponseInterceptor
    private val chain: Interceptor.Chain = mockk()
    private val request: Request = Request.Builder().url("https://example.com").build()

    @Before
    fun setup() {
        interceptor = GlobalResponseInterceptor()
        every { chain.request() } returns request
    }

    @Test
    fun `intercept returns response when successful`() {
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()
        every { chain.proceed(request) } returns response

        val result = interceptor.intercept(chain)

        assertEquals(200, result.code)
    }

    @Test(expected = UnauthorizedException::class)
    fun `intercept throws UnauthorizedException for 401`() {
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .build()
        every { chain.proceed(request) } returns response

        interceptor.intercept(chain)
    }

    @Test(expected = NotFoundException::class)
    fun `intercept throws NotFoundException for 404`() {
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(404)
            .message("Not Found")
            .build()
        every { chain.proceed(request) } returns response

        interceptor.intercept(chain)
    }

    @Test(expected = InternalServerException::class)
    fun `intercept throws InternalServerException for 500`() {
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(500)
            .message("Internal Server Error")
            .build()
        every { chain.proceed(request) } returns response

        interceptor.intercept(chain)
    }
}
