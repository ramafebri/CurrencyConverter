package com.arq.currencyconverter.core.dateformatter

import java.time.format.DateTimeParseException
import java.util.TimeZone
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DateFormatterImplTest {

    private lateinit var dateFormatter: DateFormatterImpl
    private var defaultTimeZone: TimeZone? = null

    @Before
    fun setup() {
        dateFormatter = DateFormatterImpl()
        defaultTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun tearDown() {
        defaultTimeZone?.let { TimeZone.setDefault(it) }
    }

    @Test
    fun `formatIsoDateTime formats ISO string to desired pattern`() {
        val isoString = "2023-10-27T14:30:00Z"
        val expected = "14:30, 27 October 2023"

        val result = dateFormatter.formatIsoDateTime(isoString)

        assertEquals(expected, result)
    }

    @Test
    fun `formatIsoDateTime handles ISO string without zone by assuming UTC`() {
        val isoString = "2023-10-27T14:30:00" // No 'Z' or offset
        val expected = "14:30, 27 October 2023"

        val result = dateFormatter.formatIsoDateTime(isoString)

        assertEquals(expected, result)
    }

    @Test(expected = DateTimeParseException::class)
    fun `formatIsoDateTime throws exception for invalid format`() {
        dateFormatter.formatIsoDateTime("2023/10/27 14:30:00")
    }
}
