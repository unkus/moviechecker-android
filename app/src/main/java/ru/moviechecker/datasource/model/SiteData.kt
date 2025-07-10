package ru.moviechecker.datasource.model

import java.net.URI

data class SiteData(
    val mnemonic: String,
    val address: URI,
    val title: String? = null,
    val posterLink: String? = null
)