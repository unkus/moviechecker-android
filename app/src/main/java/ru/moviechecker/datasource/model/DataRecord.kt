package ru.moviechecker.datasource.model

data class DataRecord(
    val site: SiteData,
    val movie: MovieData,
    val season: SeasonData? = null,
    val episode: EpisodeData? = null
)
