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
private const val PATTERN_TITLE =
    "<div class=\"ftop-item__title +line-clamp\">(?<title>.*)</div>"

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
    private val posterRegex = PATTERN_IMG_SRC.toRegex()
    private val titleRegex = PATTERN_TITLE.toRegex()
    private val dateTimeRegex = PATTERN_DATE_TIME.toRegex()
    private val episodeNumberRegex = PATTERN_EPISODE_NNUMBER.toRegex()

    override val mnemonic: String
        get() = "amedia"
    override val address: URI
        get() = URI.create("https://amedia.online")

    override fun retrieveData(mirror: URI?): SourceData {
        val entries = mutableListOf<SourceDataEntry>()
        val lineIterator = (mirror ?: address).toURL().openConnection()
            .apply {
                connectTimeout = 1000
                readTimeout = 3000
            }
            .getInputStream()
            .use { it.readBytes().toString(Charsets.UTF_8) }
            .lines()
            .iterator()
        val (siteTitle) = getFirstValueByRegex(lineIterator, siteTitleRegex)

        while (lineIterator.hasNext()) {
            try {
                val (href, id, pageId1, seasonNumber, pageId2) = getFirstValueByRegex(
                    lineIterator,
                    episodeLinkRegex
                )

                val (imgSrc) = getFirstValueByRegex(lineIterator, posterRegex)

                val (parsedTitle) = getFirstValueByRegex(lineIterator, titleRegex)
                val seasonTitle = parsedTitle.trim()
                val movieTitle =
                    if (seasonNumber.isNotEmpty()) seasonTitle.dropLast(seasonNumber.length + 1)
                        .trim() else seasonTitle

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
                val time =
                    if (timeString == "нестабильно") LocalTime.MIN else LocalTime.parse(timeString)
                val localDate = when (dateString) {
                    "Новая серия в", "Сегодня" -> if (LocalDateTime.now().with(time)
                            .isBefore(LocalDateTime.now())
                    ) LocalDate.now() else LocalDate.now().minusDays(1)

                    "Вчера" -> if (LocalDateTime.now().minusDays(1).with(time)
                            .isBefore(LocalDateTime.now().minusDays(1))
                    ) LocalDate.now().minusDays(1) else LocalDate.now().minusDays(2)

                    else -> LocalDate.parse(dateString, dateFormat)
                }
                val releaseTime = LocalDateTime.of(localDate, time)

                val (episodeNumber) = getFirstValueByRegex(lineIterator, episodeNumberRegex)

                val movie = MovieData(
                    pageId = pageId1.ifBlank { pageId2 },
                    title = movieTitle
                )
                Log.d(this.javaClass.simpleName, "movie=$movie")

                val season = SeasonData(
                    number = seasonNumber.ifBlank { "1" }.toInt(),
                    title = if (seasonTitle.startsWith(movieTitle)) null else seasonTitle,
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

        return SourceData(
            site = SiteData(
                mnemonic = mnemonic,
                title = siteTitle,
                address = address
            ),
            entries = entries
        )
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