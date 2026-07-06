package com.arq.currencyconverter.feature.signup.data

import com.arq.currencyconverter.core.common.UIResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignupRepositoryImplTest {

    private val localDataSource: SignupLocalDataSource = mockk()
    private lateinit var repository: SignupRepositoryImpl

    @Before
    fun setup() {
        repository = SignupRepositoryImpl(localDataSource)
    }

    @Test
    fun `signup returns Error when email already exists`() = runTest {
        coEvery { localDataSource.emailExists("test@example.com") } returns true

        val result = repository.signup("Name", "test@example.com", "password")

        assert(result is UIResult.Error)
        assertEquals("Email already registered", (result as UIResult.Error).message)
    }

    @Test
    fun `signup returns Success when account is created`() = runTest {
        coEvery { localDataSource.emailExists("test@example.com") } returns false
        coEvery { localDataSource.createAccount(any(), any(), any()) } returns Unit

        val result = repository.signup("Name", "test@example.com", "password")

        assert(result is UIResult.Success)
        coVerify { localDataSource.createAccount("Name", "test@example.com", "password") }
    }

    @Test
    fun `signup returns Error when dataSource throws exception`() = runTest {
        coEvery { localDataSource.emailExists(any()) } throws Exception("DB Error")

        val result = repository.signup("Name", "test@example.com", "password")

        assert(result is UIResult.Error)
        assertEquals("Unable to create account", (result as UIResult.Error).message)
    }
}
