package ru.moviechecker.datasource.model

data class DataContainer(
    val site: SiteData,
    val entries: List<SourceDataEntry>
)

data class SourceDataEntry(
    val movie: MovieData? = null,
    val season: SeasonData? = null,
    val episode: EpisodeData? = null,
    val error: String? = null
)