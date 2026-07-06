package com.arq.currencyconverter.core.dateformatter

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

internal class DateFormatterImpl : DateFormatter {

    override fun formatIsoDateTime(isoDateTime: String): String {
        val zonedDateTime = try {
            ZonedDateTime.parse(isoDateTime)
        } catch (_: DateTimeParseException) {
            // If the string is missing zone information (like 'Z' or '+08:00'),
            // parse as LocalDateTime and assume it's in UTC.
            LocalDateTime.parse(isoDateTime).atZone(ZoneId.of("UTC"))
        }

        val localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault())
        return OUTPUT_FORMATTER.format(localDateTime)
    }

    private companion object {
        val OUTPUT_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(
            "HH:mm, d MMMM yyyy",
            Locale.ENGLISH
        )
    }
}
