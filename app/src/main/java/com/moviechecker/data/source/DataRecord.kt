package com.moviechecker.data.source

data class DataRecord(
    val site: SiteData,
    val movie: MovieData,
    val season: SeasonData,
    val episode: EpisodeData
)
