package ru.moviechecker.datasource.model

data class SourceData(
    val site: SiteData,
    val entries: List<SourceDataEntry>

)

data class SourceDataEntry(
    val movie: MovieData,
    val season: SeasonData? = null,
    val episode: EpisodeData? = null
)