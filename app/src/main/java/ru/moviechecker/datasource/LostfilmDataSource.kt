package ru.moviechecker.datasource

import android.util.Log
import ru.moviechecker.datasource.model.SourceData
import ru.moviechecker.datasource.model.DataSource
import ru.moviechecker.datasource.model.DataState
import ru.moviechecker.datasource.model.EpisodeData
import ru.moviechecker.datasource.model.MovieData
import ru.moviechecker.datasource.model.SeasonData
import ru.moviechecker.datasource.model.SiteData
import ru.moviechecker.datasource.model.SourceDataEntry
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
// <a class="new-movie" href="/series/SurrealEstate /season_3/episode_3/" title="Сюрриэлторы">
private const val PATTERN_NEW_MOVIE_CLASS =
    "<a class=\"new-movie\" href=\"(?<href>/(?<type>series|movies)/(?<name>[^/]+)(?:/season_(?<season>\\d+)|/additional)?(?:/episode_(?<episode>\\d+))?)/?\" title=\"(?<title>.+)\">"

private const val PATTERN_OG_SITE_NAME =
    "<meta property='og:site_name' content=\"(?<siteName>.+)\" />"
private const val PATTERN_OG_TITLE = "<meta property='og:title' content=\"(?<title>.+)\" />"
private const val PATTERN_OG_IMAGE = "<meta property='og:image' content=\"(?<image>.+)\" />"
private const val PATTERN_OG_DESCRIPTION =
    "<meta property=\"og:description\" content=\"(?<description>.+)\" />"
private const val PATTERN_SEASON_POSTER_LINK = "<img src=\"(?<link>.+)\" class=\"thumb\" />"
private const val PATTERN_EPISODE_TITLE_RU = "<h1 class=\"title-ru\">(?<title>.+)</h1>"
private const val PATTERN_EPISODE_TITLE_EN = "<div class=\"title-en\">(?<title>.+)</div>"
private const val PATTERN_DATE =
    "<span data-proper=\".+\" data-released=\"(?<date>.+?)\">.*</span>"

class LostfilmDataSource : DataSource {

    private val siteTitleRegex = PATTERN_OG_SITE_NAME.toRegex()
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

    override val mnemonic: String
        get() = "lostfilm"
    override val address: URI
        get() = URI.create("https://www.lostfilm.tv")

    override fun retrieveData(mirror: URI?): SourceData {
        val content = (mirror ?: address).toURL()
            .openConnection()
            .apply {
                connectTimeout = 1000
                readTimeout = 3000
            }
            .getInputStream()
            .use { it.readBytes().toString(Charsets.UTF_8) }

        val (siteTitle) = siteTitleRegex.find(content)!!.destructured

        val entries = newMovieClassRegex.findAll(content)
            .map { matchResult ->
                val (href, typeString, name, season, episode) = matchResult.destructured
                val type = EntryType.valueOf(typeString.uppercase())
                val hrefNormalized = href.replace(" ", "")
                val nameNormalized = name.trim()
                when (type) {
                    EntryType.MOVIES -> {
                        parseMovie(
                            address = mirror ?: address,
                            pageId = nameNormalized,
                            href = hrefNormalized
                        )
                    }

                    EntryType.SERIES -> {
                        if (episode.isNotBlank()) {
                            parseSeries(
                                address = mirror ?: address,
                                pageId = nameNormalized,
                                seasonNumber = if (season.isBlank()) 999 else season.toInt(),
                                episodeNumber = episode.toInt(),
                                href = hrefNormalized
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

        return SourceData(
            site = SiteData(
                mnemonic = mnemonic,
                title = siteTitle,
                address = address
            ),
            entries = entries
        )
    }

    private fun resolveLink(address: URI, href: String): URL? {
        return address.resolve(href).toURL()
    }

    private fun parseMovie(
        address: URI,
        pageId: String,
        href: String
    ): SourceDataEntry? {
        Log.d(this.javaClass.simpleName, "Парсим фильм $pageId ($href)")
        return resolveLink(address, href)?.let { url ->
            val lineIterator = url.openConnection()
                .apply {
                    connectTimeout = 1000
                    readTimeout = 3000
                }
                .getInputStream()
                .use { it.readBytes().toString(Charsets.UTF_8) }
                .lines()
                .iterator()
            val (title) = getFirstValueByRegex(lineIterator, ogTitleRegex)
            val (posterLink) = getFirstValueByRegex(lineIterator, ogImageRegex)
//            val (description) = getFirstValueByRegex(lineIterator, ogDescriptionRegex)

            val (date) = getFirstValueByRegex(lineIterator, dateRegex)

            val movie = MovieData(
                pageId = pageId,
                title = title,
//                description = description,
                link = href,
                posterLink = posterLink
            )

            SourceDataEntry(movie = movie, season = null, episode = null)
        }
    }

    private fun parseSeries(
        address: URI,
        pageId: String,
        seasonNumber: Int,
        episodeNumber: Int,
        href: String
    ): SourceDataEntry? {
        Log.d(this.javaClass.simpleName, "Парсим эпизод $pageId (${href})")
        return resolveLink(address, href)?.let { url ->
            val lineIterator = url.openConnection()
                .apply {
                    connectTimeout = 1000
                    readTimeout = 3000
                }
                .getInputStream()
                .use { it.readBytes().toString(Charsets.UTF_8) }
                .lines()
                .iterator()
            val (seriesTitle) = getFirstValueByRegex(lineIterator, ogTitleRegex)
            val (seriesPosterLink) = getFirstValueByRegex(lineIterator, ogImageRegex)
//            val (episodeDescription) = getFirstValueByRegex(
//                lineIterator,
//                ogDescriptionRegex
//            )
            val (seasonPosterLink) = getFirstValueByRegex(lineIterator, seasonPosterLinkRegex)
            val (episodeTitleRu) = getFirstValueByRegex(lineIterator, ruEpisodeTitleRegex)
            val (episodeTitleEn) = getFirstValueByRegex(lineIterator, enEpisodeTitleRegex)
            val (episodeDate) = getFirstValueByRegex(lineIterator, dateRegex)

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

            SourceDataEntry(movie, season, episode)
        }
    }

    private fun getFirstValueByRegex(
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