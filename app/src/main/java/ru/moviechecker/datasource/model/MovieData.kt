package ru.moviechecker.datasource.model

data class MovieData (
    val mnemonic: String,
    val title: String,
    val description: String? = null,
    val link: String? = null,
    val posterLink: String? = null
)