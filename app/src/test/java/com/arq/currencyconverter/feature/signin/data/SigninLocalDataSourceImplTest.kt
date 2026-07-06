package com.arq.currencyconverter.feature.signin.data

import com.arq.currencyconverter.core.data.local.db.UserAccountDao
import com.arq.currencyconverter.core.data.local.db.UserAccountEntity
import com.arq.currencyconverter.core.data.local.session.UserSessionPreferences
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
class SigninLocalDataSourceImplTest {

    private val userAccountDao: UserAccountDao = mockk()
    private val userSessionPreferences: UserSessionPreferences = mockk(relaxed = true)
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataSource: SigninLocalDataSourceImpl

    @Before
    fun setup() {
        dataSource = SigninLocalDataSourceImpl(
            userAccountDao,
            userSessionPreferences,
            testDispatcher,
            testDispatcher
        )
    }

    @Test
    fun `signIn returns InvalidCredentials when user not found`() = runTest {
        coEvery { userAccountDao.getByEmail("test@example.com") } returns null

        val result = dataSource.signIn("test@example.com", "password")

        assertEquals(SigninResult.InvalidCredentials, result)
    }

    @Test
    fun `signIn returns InvalidCredentials when password hash does not match`() = runTest {
        val user = UserAccountEntity(1, "Test User", "test@example.com", "different_hash", "salt")
        coEvery { userAccountDao.getByEmail("test@example.com") } returns user

        val result = dataSource.signIn("test@example.com", "password")

        assertEquals(SigninResult.InvalidCredentials, result)
    }

    @Test
    fun `signIn returns Success and saves session when credentials are valid`() = runTest {
        val password = "password"
        val hashedPassword = PasswordHasher.hash(password)
        val user = UserAccountEntity(
            1,
            "Test User",
            "test@example.com",
            hashedPassword,
            PasswordHasher.SALT
        )
        coEvery { userAccountDao.getByEmail("test@example.com") } returns user

        val result = dataSource.signIn("test@example.com", password)

        assert(result is SigninResult.Success)
        assertEquals(1L, (result as SigninResult.Success).userId)
        coVerify { userSessionPreferences.saveUser(1, "Test User", "test@example.com") }
    }
}
