package com.example.moviechecker.model

import android.net.Uri
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

import java.time.ZonedDateTime


class Converters {

    @TypeConverter
    fun uriFromString(value: String?): Uri? = value?.let { Uri.parse(it) }

    @TypeConverter
    fun stringToUri(value: Uri?): String? = value?.toString()

    @TypeConverter
    fun localDataAndTimeFromTimestamp(value: Long?): LocalDateTime? =
        value?.let { Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault()) // default zone
            .toLocalDateTime() }

    @TypeConverter
    fun timestampToLocalDataAndTime(value: LocalDateTime?): Long? =
        value?.let { ZonedDateTime.of(it, ZoneId.systemDefault()).toInstant().toEpochMilli() }
}