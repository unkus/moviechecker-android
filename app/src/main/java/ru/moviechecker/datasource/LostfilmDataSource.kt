package ru.moviechecker.datasource

import android.util.Log
import ru.moviechecker.datasource.model.DataRecord
import ru.moviechecker.datasource.model.DataSource
import ru.moviechecker.datasource.model.DataState
import ru.moviechecker.datasource.model.EpisodeData
import ru.moviechecker.datasource.model.MovieData
import ru.moviechecker.datasource.model.SeasonData
import ru.moviechecker.datasource.model.SiteData
import java.net.URI
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// <a class="new-movie" href="/series/A_Thousand_Blows/season_1/episode_1/" title="Тысяча ударов">
// <a class="new-movie" href="/series/Euphoria/additional/episode_1/" title="Эйфория">
// <a class="new-movie" href="/movies/Mufasa_The_Lion_King" title="Муфаса: Король Лев">
// <a class="new-movie" href="/series/The_Head/season_3/" title="Голова">
private const val PATTERN_NEW_MOVIE_CLASS =
    "<a class=\"new-movie\" href=\"(?<href>/(?<type>series|movies)/(?<name>[^/]+)(?:/season_(?<season>\\d+)|/additional)?(?:/episode_(?<episode>\\d+))?)/?\" title=\"(?<title>.+)\">"

private const val PATTERN_OG_TITLE = "<meta property='og:title' content=\"(?<title>.+)\" />"
private const val PATTERN_OG_IMAGE = "<meta property='og:image' content=\"(?<image>.+)\" />"
private const val PATTERN_OG_DESCRIPTION =
    "<meta property=\"og:description\" content=\"(?<description>.+[^ ]) *\" />"
private const val PATTERN_SEASON_POSTER_LINK = "<img src=\"(?<link>.+)\" class=\"thumb\" />"
private const val PATTERN_EPISODE_TITLE_RU = "<h1 class=\"title-ru\">(?<title>.+)</h1>"
private const val PATTERN_EPISODE_TITLE_EN = "<div class=\"title-en\">(?<title>.+)</div>"
private const val PATTERN_DATE =
    "<span data-proper=\"0\" data-released=\"(?<date>.+)\">.*</span> г.<br/>"

class LostfilmDataSource : DataSource {

    private val newMovieClassRegex = PATTERN_NEW_MOVIE_CLASS.toRegex(RegexOption.MULTILINE)
    private val ogTitleRegex = PATTERN_OG_TITLE.toRegex()
    private val ogImageRegex = PATTERN_OG_IMAGE.toRegex()
    private val ogDescriptionRegex = PATTERN_OG_DESCRIPTION.toRegex()
    private val seasonPosterLinkRegex = PATTERN_SEASON_POSTER_LINK.toRegex()
    private val ruEpisodeTitleRegex = PATTERN_EPISODE_TITLE_RU.toRegex()
    private val enEpisodeTitleRegex = PATTERN_EPISODE_TITLE_EN.toRegex()
    private val dateRegex = PATTERN_DATE.toRegex()

    private val dateFormat =
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("ru-RU"))

    override val site: SiteData
        get() = SiteData(URI.create("https://www.lostfilm.download"))

    override fun retrieveData(): Collection<DataRecord> {
        return newMovieClassRegex.findAll(site.address.toURL().readText())
            .map { matchResult ->
                val (href, typeString, name, season, episode) = matchResult.destructured
                val type = EntryType.valueOf(typeString.uppercase())
                when (type) {
                    EntryType.MOVIES -> {
                        parseMovie(pageId = name, href = href)
                    }

                    EntryType.SERIES -> {
                        if (episode.isNotBlank()) {
                            parseSeries(
                                pageId = name,
                                seasonNumber = if (season.isBlank()) 999 else season.toInt(),
                                episodeNumber = episode.toInt(),
                                href = href
                            )
                        } else {
                            // только сезоны нам не нужны
                            null
                        }
                    }
                }
            }
            .filterNotNull()
            .onEach {
                Log.d(this.javaClass.simpleName, "movie=${it.movie}")
                Log.d(this.javaClass.simpleName, "season=${it.season}")
                Log.d(this.javaClass.simpleName, "episode=${it.episode}")
            }
            .toList()
    }

    private fun resolveLink(href: String): URL? {
        return site.address.resolve(href).toURL()
    }

    private fun parseMovie(
        pageId: String,
        href: String
    ): DataRecord? {
        return resolveLink(href)?.let { url ->
            val lineIterator = url.readText().lines().iterator()
            val (title) = getFirstValueByPattern(lineIterator, ogTitleRegex)
            val (posterLink) = getFirstValueByPattern(lineIterator, ogImageRegex)
//            val (description) = getFirstValueByPattern(lineIterator, ogDescriptionRegex)

            val (date) = getFirstValueByPattern(lineIterator, dateRegex)

            val movie = MovieData(
                pageId = pageId,
                title = title,
                link = href,
                posterLink = posterLink
            )

            DataRecord(movie = movie, season = null, episode = null)
        }
    }

    private fun parseSeries(
        pageId: String,
        seasonNumber: Int,
        episodeNumber: Int,
        href: String
    ): DataRecord? {
        return resolveLink(href)?.let { url ->
            val lineIterator = url.readText().lines().iterator()
            val (seriesTitle) = getFirstValueByPattern(lineIterator, ogTitleRegex)
            val (seriesPosterLink) = getFirstValueByPattern(lineIterator, ogImageRegex)
//            val (episodeDescription) = getFirstValueByPattern(
//                lineIterator,
//                ogDescriptionRegex
//            )
            val (seasonPosterLink) = getFirstValueByPattern(lineIterator, seasonPosterLinkRegex)
            val (episodeTitleRu) = getFirstValueByPattern(lineIterator, ruEpisodeTitleRegex)
            val (episodeTitleEn) = getFirstValueByPattern(lineIterator, enEpisodeTitleRegex)
            val (episodeDate) = getFirstValueByPattern(lineIterator, dateRegex)

            val movie = MovieData(
                pageId = pageId,
                title = seriesTitle,
                link = href.split("/").take(3).joinToString(separator = "/"),
                posterLink = seriesPosterLink
            )

            val season = SeasonData(
                number = seasonNumber,
                link = href.split("/").take(4).joinToString(separator = "/"),
                posterLink = seasonPosterLink
            )

            val episode = EpisodeData(
                number = episodeNumber,
                title = episodeTitleRu,
//                description = episodeDescription,
                link = href,
                date = LocalDateTime.of(
                    LocalDate.parse(episodeDate, dateFormat), LocalTime.MIN
                ),
                state = DataState.RELEASED
            )

            DataRecord(movie, season, episode)
        }
    }

    private fun getFirstValueByPattern(
        iterator: Iterator<String>,
        regex: Regex
    ): MatchResult.Destructured {
        return iterator.asSequence()
            .firstNotNullOf {
                regex.find(it)?.destructured
            }
    }

}

private enum class EntryType {
    MOVIES,
    SERIES
}