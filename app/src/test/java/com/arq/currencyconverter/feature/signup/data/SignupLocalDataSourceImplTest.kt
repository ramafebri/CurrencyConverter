package com.arq.currencyconverter.feature.signup.data

import com.arq.currencyconverter.core.data.local.db.UserAccountDao
import com.arq.currencyconverter.core.security.PasswordHasher
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignupLocalDataSourceImplTest {

    private val userAccountDao: UserAccountDao = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataSource: SignupLocalDataSourceImpl

    @Before
    fun setup() {
        dataSource = SignupLocalDataSourceImpl(
            userAccountDao,
            testDispatcher,
            testDispatcher
        )
    }

    @Test
    fun `createAccount hashes password and inserts user into DAO`() = runTest {
        coEvery { userAccountDao.insert(any()) } returns 1L

        dataSource.createAccount("Test User", "test@example.com", "password")

        coVerify {
            userAccountDao.insert(
                match {
                    it.name == "Test User" &&
                        it.email == "test@example.com" &&
                        it.hashedPassword == PasswordHasher.hash("password")
                }
            )
        }
    }

    @Test
    fun `emailExists returns true when email exists in DAO`() = runTest {
        coEvery { userAccountDao.emailExists("test@example.com") } returns true

        val exists = dataSource.emailExists("test@example.com")

        assertEquals(true, exists)
    }
}
