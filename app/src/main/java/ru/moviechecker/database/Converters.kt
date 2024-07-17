package ru.moviechecker.database

import androidx.room.TypeConverter
import java.net.URI
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

import java.time.ZonedDateTime


class Converters {

    @TypeConverter
    fun uriFromString(value: String?): URI? = value?.let { URI.create(it) }

    @TypeConverter
    fun stringToUri(value: URI?): String? = value?.toString()

    @TypeConverter
    fun localDataAndTimeFromTimestamp(value: Long?): LocalDateTime? =
        value?.let { Instant.ofEpochMilli(it)
            // TODO: достать в UTC и перевести в System
            .atZone(ZoneId.systemDefault()) // default zone
            .toLocalDateTime() }

    @TypeConverter
    fun timestampToLocalDataAndTime(value: LocalDateTime?): Long? =
        // TODO: перевести из System в UTC
        value?.let { ZonedDateTime.of(it, ZoneId.systemDefault()).toInstant().toEpochMilli() }
}