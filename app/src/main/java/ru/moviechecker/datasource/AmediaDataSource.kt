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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


// <title>Animedia Online - Смотреть аниме онлайн!</title>
private const val PATTERN_SITE_TITLE = "<title>(?<title>.*) -.*</title>"

// <a class="ftop-item d-flex has-overlay" href="/1593-moj-djejmon.html">
private const val PATTERN_EPISODE_LINK =
    "<a class=\"ftop-item d-flex has-overlay\" href=\"(?<href>/(?<id>\\d+)-(?:(?<pageId1>.+)-(?<seasonNumber>\\d+)|(?<pageId2>.+))\\.html)\">"

// <img src="/uploads/posts/2023-12/thumbs/fhk1xclgnqldcwyf__6895b8df64dcf260929c7c58a83a81e7.webp" alt="постер к аниме Мой Дэймон" >
private const val PATTERN_IMG_SRC = "<img src=\"(?<imgSrc>.*)\" alt=\".*\""

// <div class="ftop-item__title  line-clamp">Мой Дэймон </div>
private const val PATTERN_SEASON_TITLE =
    "<div class=\"ftop-item__title +line-clamp\">(?<title>.*[^ ]) ?</div>"

// <div class="ftop-item__meta poster__subtitle line-clamp">Сегодня, 17:13</div>
private const val PATTERN_DATE_TIME =
    "<div class=\"ftop-item__meta poster__subtitle line-clamp\">(?<date>.+)(?:, | <span>)(?<time>\\d{1,2}:\\d{1,2}|нестабильно)(?:</span>)?</div>"

// <div class="animseri"><span>13</span>серия</div>
private const val PATTERN_EPISODE_NNUMBER =
    "<div class=\"animseri\"><span>(?<episodeNumber>\\d+)?(?:-\\d+)?</span>серия</div>"

class AmediaDataSource : DataSource {

    private val dateFormat = DateTimeFormatter.ofPattern("d-MM-yyyy")

    private val siteTitleRegex = PATTERN_SITE_TITLE.toRegex()
    private val episodeLinkRegex = PATTERN_EPISODE_LINK.toRegex()
    private val seasonPosterRegex = PATTERN_IMG_SRC.toRegex()
    private val seasonTitleRegex = PATTERN_SEASON_TITLE.toRegex()
    private val dateTimeRegex = PATTERN_DATE_TIME.toRegex()
    private val episodeNumberRegex = PATTERN_EPISODE_NNUMBER.toRegex()

    override val address: URI
        get() = URI.create("https://amedia.lol")

    override fun retrieveData(): SourceData {
        val entries = mutableListOf<SourceDataEntry>()
        val lineIterator = address.toURL().readText().lines().iterator()
        val (siteTitle) = getFirstValueByRegex(lineIterator, siteTitleRegex)

        while (lineIterator.hasNext()) {
            try {
                val (href, id, pageId1, seasonNumber, pageId2) = getFirstValueByRegex(
                    lineIterator,
                    episodeLinkRegex
                )

                val (imgSrc) = getFirstValueByRegex(lineIterator, seasonPosterRegex)

                val (seasonTitle) = getFirstValueByRegex(lineIterator, seasonTitleRegex)
                val movieTitle =
                    if (seasonNumber.isNotEmpty()) seasonTitle.dropLast(seasonNumber.length + 1) else seasonTitle

                val (dateString, timeString) = getFirstValueByRegex(lineIterator, dateTimeRegex)
                Log.d(this.javaClass.simpleName, "$seasonTitle - $dateString $timeString")

                // @formatter:off
                /*
                 * возможные варианты:
                 *
                 * Сегодня, 19:43
                 * Вчера, 22:49
                 * 31-01-2023, 19:49
                 * Новая серия в <span>15:00</span>
                 * Новая серия в нестабильно
                 *
                 */
                // @formatter:on
                val localDate = when (dateString) {
                    "Новая серия в" -> LocalDate.now()
                    "Сегодня" -> LocalDate.now()
                    "Вчера" -> LocalDate.now().minusDays(1)
                    else -> LocalDate.parse(dateString, dateFormat)
                }
                val releaseTime = LocalDateTime.of(
                    localDate,
                    if (timeString == "нестабильно") LocalTime.MIN else LocalTime.parse(timeString)
                )

                val (episodeNumber) = getFirstValueByRegex(lineIterator, episodeNumberRegex)

                val movie = MovieData(
                    pageId = pageId1.ifBlank { pageId2 },
                    title = movieTitle
                )
                Log.d(this.javaClass.simpleName, "movie=$movie")

                val season = SeasonData(
                    number = seasonNumber.ifBlank { "1" }.toInt(),
                    title = seasonTitle,
                    link = href,
                    posterLink = imgSrc
                )
                Log.d(this.javaClass.simpleName, "season=$season")

                val episode = EpisodeData(
                    number = episodeNumber.toInt(),
                    link = "/$id-${pageId1.ifBlank { pageId2 }}${if (seasonNumber.isNotBlank()) "-$seasonNumber" else ""}/episode/$episodeNumber/seriya-onlayn.html",
                    date = releaseTime,
                    state = if (dateString == "Новая серия в") DataState.EXPECTED else DataState.RELEASED
                )
                Log.d(this.javaClass.simpleName, "episode=$episode")

                entries.add(
                    SourceDataEntry(
                        movie = movie,
                        season = season,
                        episode = episode
                    )
                )
            } catch (ex: Exception) {
                // Ничего не делаем
                Log.d(this.javaClass.simpleName, "Ошибка при парсинге: ${ex.message}")
            }
        }

        Log.i(this.javaClass.simpleName, "данные получены от $address")
        return SourceData(
            site = SiteData(
                title = siteTitle,
                address = address
            ),
            entries = entries)
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