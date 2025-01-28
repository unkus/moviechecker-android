package ru.moviechecker.datasource

import android.util.Log
import androidx.core.text.isDigitsOnly
import ru.moviechecker.datasource.model.DataRecord
import ru.moviechecker.datasource.model.DataSource
import ru.moviechecker.datasource.model.DataState
import ru.moviechecker.datasource.model.EpisodeData
import ru.moviechecker.datasource.model.MovieData
import ru.moviechecker.datasource.model.SeasonData
import ru.moviechecker.datasource.model.SiteData
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// <a class="ftop-item d-flex has-overlay" href="/1593-moj-djejmon.html">
private const val PATTERN_EPISODE_LINK =
    "<a class=\"ftop-item d-flex has-overlay\" href=\"(?<seasonLink>/(?<seasonPageId>\\d+-(?<moviePageId>\\w+(?:-(?<seasonNumber>\\w+))*))\\.html)\">"

// <img src="/uploads/posts/2023-12/thumbs/fhk1xclgnqldcwyf__6895b8df64dcf260929c7c58a83a81e7.webp" alt="постер к аниме Мой Дэймон" >
private const val PATTERN_SEASON_POSTER =
    "<img src=\"(?<seasonPosterLink>.+)\" alt=\"постер к аниме .+\" >"

// <div class="ftop-item__title  line-clamp">Мой Дэймон </div>
private const val PATTERN_SEASON_TITLE =
    "<div class=\"ftop-item__title +line-clamp\">(?<seasonTitle>.+)</div>"

// <div class="ftop-item__meta poster__subtitle line-clamp">Сегодня, 17:13</div>
private const val PATTERN_DATE_TIME =
    "<div class=\"ftop-item__meta poster__subtitle line-clamp\">(?<date>.+)(?:, | <span>)(?<time>\\d{1,2}:\\d{1,2}|нестабильно)(?:</span>)?</div>"

// <div class="animseri"><span>13</span>серия</div>
private const val PATTERN_EPISODE_NNUMBER =
    "<div class=\"animseri\"><span>(?<episodeNumber>\\d+)?(?:-\\d+)?</span>серия</div>"

class AmediaDataSource : DataSource {

    private val site = SiteData(URI.create("https://amedia.lol")) //SiteData(URI.create("https://amedia.online"))

    private val dateFormat = DateTimeFormatter.ofPattern("d-MM-yyyy")

    private val episodeLinkRegex = PATTERN_EPISODE_LINK.toRegex()
    private val seasonPosterRegex = PATTERN_SEASON_POSTER.toRegex()
    private val seasonTitleRegex = PATTERN_SEASON_TITLE.toRegex()
    private val dateTimeRegex = PATTERN_DATE_TIME.toRegex()
    private val episodeNumberRegex = PATTERN_EPISODE_NNUMBER.toRegex()

    override fun retrieveData(): Collection<DataRecord> {
        Log.i(this.javaClass.simpleName, "Получаем данные от ${site.address}")
        val dataList = mutableListOf<DataRecord>()

        val lineIterator =
            site.address.toURL().readText().reader().buffered().readLines().iterator()
        var line: String
        while (lineIterator.hasNext()) {
            while (lineIterator.hasNext()) {
                line = lineIterator.next().trim()
                episodeLinkRegex.find(line)?.let { it ->
                    val movie = MovieData.Builder()
                    val season = SeasonData.Builder()
                    val episode = EpisodeData.Builder()

                    Log.d(this.javaClass.simpleName, line)
                    var (_, seasonLink, seasonPageId, moviePageId, seasonNumber) = it.groupValues
                    Log.d(
                        this.javaClass.simpleName,
                        "seasonLink: $seasonLink, seasonPageId: $seasonPageId, moviePageId: $moviePageId, seasonNumber: $seasonNumber"
                    )
                    season.link(seasonLink)
                    if (seasonNumber.isNotBlank() && seasonNumber.isDigitsOnly()) {
                        moviePageId = moviePageId.replace("-$seasonNumber", "")
                        season.number(seasonNumber.toInt())
                    } else {
                        seasonNumber = ""
                        season.number(1)
                    }
                    movie.pageId(moviePageId)

                    lineIterator.next() // Skip line

                    line = lineIterator.next().trim()
                    Log.d(this.javaClass.simpleName, line)
                    seasonPosterRegex.find(line)?.let { matchResult ->
                        val (_, seasonPosterLink) = matchResult.groupValues
                        Log.d(
                            this.javaClass.simpleName,
                            "seasonPosterLink: $seasonPosterLink"
                        )
                        season.posterLink(seasonPosterLink)
                    }

                    lineIterator.next() // Skip line
                    lineIterator.next() // Skip line

                    line = lineIterator.next().trim()
                    Log.d(this.javaClass.simpleName, line)
                    seasonTitleRegex.find(line)?.let { matchResult ->
                        val (_, seasonTitle) = matchResult.groupValues
                        Log.d(this.javaClass.simpleName, "seasonTitle: $seasonTitle")
                        movie.title(seasonTitle.substringBeforeLast(" $seasonNumber"))
                        season.title(seasonTitle.trim())
                    }

                    line = lineIterator.next().trim()
                    Log.d(this.javaClass.simpleName, line)
                    dateTimeRegex.find(line)?.let { matchResult ->
                        val (_, date, time) = matchResult.groupValues
                        Log.d(this.javaClass.simpleName, "date: $date, time: $time")
                        // @formatter:off
                        /*
                         * possible variants:
                         *
                         * Сегодня, 19:43
                         * Вчера, 22:49
                         * 31-01-2023, 19:49
                         * Новая серия в <span>15:00</span>
                         * Новая серия в нестабильно
                         *
                         */
                        // @formatter:on
                        val localDate = when (date) {
                            "Новая серия в" -> {
                                episode.state(DataState.EXPECTED)
                                LocalDate.now()
                            }

                            "Сегодня" -> {
                                episode.state(DataState.RELEASED)
                                LocalDate.now()
                            }

                            "Вчера" -> {
                                episode.state(DataState.RELEASED)
                                LocalDate.now().minusDays(1)
                            }

                            else -> {
                                episode.state(DataState.RELEASED)
                                LocalDate.parse(date, dateFormat)
                            }
                        }
                        time.let { t ->
                            if (t == "нестабильно") {
                                episode.date(LocalDate.now().atStartOfDay())
                            } else {
                                episode.date(
                                    LocalDateTime.of(
                                        localDate, LocalTime.parse(t)
                                    )
                                )
                            }
                        }
                    }

                    lineIterator.next() // Skip line

                    line = lineIterator.next().trim()
                    Log.d(this.javaClass.simpleName, line)

                    episodeNumberRegex.find(line)?.let { matchResult ->
                        val (_, episodeNumber) = matchResult.groupValues
                        Log.d(this.javaClass.simpleName, "episode: $episodeNumber")
                        episodeNumber.ifEmpty {
                            episode.number(-1)
                            episode.link("/$seasonPageId/episode/1/seriya-onlayn.html")
                        }
                        episode.number(episodeNumber.ifEmpty { "-1" }.toInt())
                        episode.link("/$seasonPageId/episode/$episodeNumber/seriya-onlayn.html")
                    }

                    if (movie.validate() && season.validate() && episode.validate()) {
                        val data = DataRecord(
                            site, movie.build(), season.build(), episode.build()
                        )
                        dataList.add(data)
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
        }
        Log.i(this.javaClass.simpleName, "данные получены от ${site.address}")
        return dataList
    }
}