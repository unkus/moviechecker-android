package ru.moviechecker.datasource.model

import java.time.LocalDateTime

data class EpisodeData(
    val number: Int,
    val title: String? = null,
    val description: String? = null,
    val link: String,
    val state: DataState,
    val date: LocalDateTime
)