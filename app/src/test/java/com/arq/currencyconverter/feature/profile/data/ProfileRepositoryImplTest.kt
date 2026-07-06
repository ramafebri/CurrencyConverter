package com.arq.currencyconverter.feature.profile.data

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
class ProfileRepositoryImplTest {

    private val localDataSource: ProfileLocalDataSource = mockk()
    private lateinit var repository: ProfileRepositoryImpl

    @Before
    fun setup() {
        repository = ProfileRepositoryImpl(localDataSource)
    }

    @Test
    fun `logout returns Success when dataSource succeeds`() = runTest {
        coEvery { localDataSource.logout() } returns Unit

        val result = repository.logout()

        assert(result is UIResult.Success)
        coVerify { localDataSource.logout() }
    }

    @Test
    fun `logout returns Error when dataSource throws exception`() = runTest {
        coEvery { localDataSource.logout() } throws Exception("Logout failed")

        val result = repository.logout()

        assert(result is UIResult.Error)
        assertEquals("Unable to log out", (result as UIResult.Error).message)
    }
}
