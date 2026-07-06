package com.arq.currencyconverter.feature.signin.data

import com.arq.currencyconverter.core.common.UIResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SigninRepositoryImplTest {

    private val localDataSource: SigninLocalDataSource = mockk()
    private lateinit var repository: SigninRepositoryImpl

    @Before
    fun setup() {
        repository = SigninRepositoryImpl(localDataSource)
    }

    @Test
    fun `signIn returns Success when dataSource returns Success`() = runTest {
        coEvery { localDataSource.signIn(any(), any()) } returns SigninResult.Success(1L)

        val result = repository.signIn("test@example.com", "password")

        assert(result is UIResult.Success)
    }

    @Test
    fun `signIn returns Error when dataSource returns InvalidCredentials`() = runTest {
        coEvery { localDataSource.signIn(any(), any()) } returns SigninResult.InvalidCredentials

        val result = repository.signIn("test@example.com", "password")

        assert(result is UIResult.Error)
        assertEquals("Invalid email or password", (result as UIResult.Error).message)
    }

    @Test
    fun `signIn returns Error when dataSource throws exception`() = runTest {
        coEvery { localDataSource.signIn(any(), any()) } throws Exception("DB Error")

        val result = repository.signIn("test@example.com", "password")

        assert(result is UIResult.Error)
        assertEquals("Unable to sign in", (result as UIResult.Error).message)
    }
}
