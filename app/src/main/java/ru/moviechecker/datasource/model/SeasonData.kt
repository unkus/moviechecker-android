package ru.moviechecker.datasource.model

data class SeasonData(
    val number: Int,
    val title: String? = null,
    val link: String,
    val posterLink: String? = null
)