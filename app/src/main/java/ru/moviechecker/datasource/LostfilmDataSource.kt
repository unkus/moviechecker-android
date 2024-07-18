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

private const val PATTERN_NEW_MOVIE_CLASS =
    "<a class=\"new-movie\" href=\"(?<href>.+)/\" title=\"(?<title>.+)\">"

private const val PATTERN_TODAY_CLASS = "<td class=\"today\">"

private const val PATTERN_EPISODE_LINK =
    "<a href=\"(?<href>.+)/\" class=\"title\">(?<title>.+)</br>"

private const val PATTERN_BREADCRUMBS_PANE = "<div class=\"breadcrumbs-pane\">"

// <a href="/series/Monarch_Legacy_of_Monsters/" class="item">Монарх: Наследие монстров</a>
private const val PATTERN_MOVIE_PATH =
    "<a href=\"(?<moviePath>/series/(?<moviePageId>.+))/\" class=\"item\">(?<movieTitle>.+)</a>"

// <a href="/series/Monarch_Legacy_of_Monsters/season_1/" class="item"><div class="arrow"></div>1 сезон</a>
private const val PATTERN_SEASON_PATH =
    "<a href=\"(?<seasonPath>.+)/\" class=\"item\"><div class=\"arrow\"></div>(?<seasonNumber>\\d+) сезон</a>"

// <a href="/series/Monarch_Legacy_of_Monsters/season_1/episode_10/" class="item"><div class="arrow"></div>10 серия</a>
private const val PATTERN_EPISODE_PATH =
    "<a href=\"(?<episodePath>.+)/\" class=\"item\"><div class=\"arrow\"></div>(?<episodeNumber>\\d+) серия</a>"

private const val PATTERN_SERIA_HEADER = "<div class=\"seria-header\">"

private const val PATTERN_POSTER_LINK = "<img src=\"(?<posterRef>.+)\" class=\"thumb\" */>"

private const val PATTERN_RU_EPISODE_TITLE = "<h1 class=\"title-ru\">(?<episodeTitle>.+)</h1>"

private const val PATTERN_EN_EPISODE_TITLE = "<div class=\"title-en\">(?<episodeTitle>.+)</div>"

private const val PATTERN_EXPECTED_DATE = "<div class=\"expected\">Ожидается (?<date>.+)</div>"

private const val PATTERN_DATE =
    "<span data-proper=\".+\" data-released=\"(?<date>\\d{1,2} .+ \\d{4})\">.+</span>"

class LostfilmDataSource : DataSource {

    private val site = SiteData(URI.create("https://www.lostfilm.download"))

    private val newMovieClassRegex = PATTERN_NEW_MOVIE_CLASS.toRegex()
    private val todayClassRegex = PATTERN_TODAY_CLASS.toRegex()
    private val episodeLinkRegex = PATTERN_EPISODE_LINK.toRegex()
    private val breadcrumbsClassRegex = PATTERN_BREADCRUMBS_PANE.toRegex()
    private val moviePathRegex = PATTERN_MOVIE_PATH.toRegex()
    private val seasonPathRegex = PATTERN_SEASON_PATH.toRegex()
    private val episodePathRegex = PATTERN_EPISODE_PATH.toRegex()
    private val seriaHeaderClassRegex = PATTERN_SERIA_HEADER.toRegex()
    private val thumbsClassRegex = PATTERN_POSTER_LINK.toRegex()
    private val ruEpisodeTitleRegex = PATTERN_RU_EPISODE_TITLE.toRegex()
    private val enEpisodeTitleRegex = PATTERN_EN_EPISODE_TITLE.toRegex()
    private val expectedDateRegex = PATTERN_EXPECTED_DATE.toRegex()
    private val dateRegex = PATTERN_DATE.toRegex()

    private val dateFormat =
        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("ru-RU"))

    override suspend fun retrieveData(): Collection<DataRecord> {
        Log.i(this.javaClass.simpleName, "Получаем данные от ${site.address}")
        val recordList = mutableSetOf<DataRecord>()

        val lineIterator =
            site.address.toURL().readText().reader().buffered().readLines().iterator()
        while (lineIterator.hasNext()) {
            val inputLine = lineIterator.next()
            var href: String? = null
            newMovieClassRegex.find(inputLine)?.let { matchResult ->
                href = matchResult.groups["href"]?.value
            }
            todayClassRegex.find(inputLine)?.let {
                lineIterator.next() // skip tag
                episodeLinkRegex.find(lineIterator.next())?.let { matchResult ->
                    href = matchResult.groups["href"]?.value
                }
            }
            href?.let {
                ".+episode_\\d+".toRegex().find(it)?.let {
                    getEpisodeDetails(site.address.resolve(href).toURL())?.let(recordList::add)
                }
            }
        }
        Log.i(this.javaClass.simpleName, "данные получены от ${site.address}")
        return recordList
    }

    private fun getEpisodeDetails(url: URL): DataRecord? {
        val movie = MovieData.Builder()
        val season = SeasonData.Builder()
        val episode = EpisodeData.Builder(state = DataState.RELEASED)

        val lineIterator = url.readText().reader().buffered().readLines().iterator()
        while (lineIterator.hasNext()) {
            var inputLine = lineIterator.next().trim()
            // <div class="breadcrumbs-pane">
            breadcrumbsClassRegex.find(inputLine)?.let {
                moviePathRegex.find(lineIterator.next())?.let { matchResult ->
                    val (_, moviePath, moviePageId, movieTitle) = matchResult.groupValues
                    Log.d(this.javaClass.simpleName, "Парсим данные для $moviePageId")
                    movie.pageId(moviePageId)
                    movie.link(site.address.resolve(moviePath))
                    movie.title(movieTitle)
                }
                // <a href="/series/Monarch_Legacy_of_Monsters/seasons/" class="item"><div class="arrow"></div>Гид по сериям</a>
                lineIterator.next() // skip
                inputLine = lineIterator.next().trim()
                Log.d(this.javaClass.simpleName, inputLine)
                seasonPathRegex.find(inputLine)?.let { matchResult ->
                    val (_, seasonPath, seasonNumber) = matchResult.groupValues
                    Log.d(this.javaClass.simpleName, "season $seasonPath ($seasonNumber)")
                    season.link(site.address.resolve(seasonPath))
                    season.number(seasonNumber.toInt())
                }
                inputLine = lineIterator.next().trim()
                Log.d(this.javaClass.simpleName, inputLine)
                episodePathRegex.find(inputLine)?.let { matchResult ->
                    val (_, episodePath, episodeNumber) = matchResult.groupValues
                    Log.d(this.javaClass.simpleName, "episode $episodePath ($episodeNumber)")
                    episode.link(site.address.resolve(episodePath))
                    episode.number(episodeNumber.toInt())
                }
            }
            // <div class="seria-header">
            seriaHeaderClassRegex.find(inputLine)?.let {
                // <img src="//static.lostfilm.top/Images/791/Posters/icon_s1.jpg" class="thumb">
                thumbsClassRegex.find(lineIterator.next())?.let { matchResult ->
                    val (_, posterRef) = matchResult.groupValues
                    movie.posterLink(site.address.resolve(URI.create(posterRef)))
                }
                // <h1 class="title-ru">За гранью логики</h1>
                ruEpisodeTitleRegex.find(lineIterator.next())?.let { matchResult ->
                    matchResult.groups["episodeTitle"]?.let { matchGroup ->
                        episode.title(matchGroup.value)
                    }
                }
                // <div class="title-en">Beyond Logic</div>
                lineIterator.next()// skip, is this data needed me?
                // titleEnClassRegex.find(lineIterator.next())?.let { matchResult->
                // }
            }
            expectedDateRegex.find(inputLine)?.let {
                episode.state(DataState.EXPECTED)
            }
            // <span data-proper="0" data-released="14 января 2024">14 января 2024</span>
            dateRegex.find(inputLine)?.let { matchResult ->
                val (_, date) = matchResult.groupValues
                Log.d(this.javaClass.simpleName, "date: $date")
                episode.date(
                    LocalDateTime.of(
                        LocalDate.parse(date, dateFormat), LocalTime.MIN
                    )
                )
                if (movie.validate() && season.validate() && episode.validate()) {
                    return DataRecord(site, movie.build(), season.build(), episode.build())
                } else {
                    if (!movie.validate()) {
                        Log.e(this.javaClass.simpleName, "invalid movie data: $movie")
                    }
                    if (!season.validate()) {
                        Log.e(this.javaClass.simpleName, "invalid season data: $season")
                    }
                    if (!episode.validate()) {
                        Log.e(this.javaClass.simpleName, "invalid episode data: $episode")
                    }
                    Log.e(this.javaClass.simpleName, "unexpected error")
                }
            }
        }
        throw Exception("No record produced for $url")
    }
}