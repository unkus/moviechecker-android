package ru.moviechecker.datasource

import android.util.Log
import ru.moviechecker.datasource.model.DataState
import ru.moviechecker.datasource.model.EpisodeData
import ru.moviechecker.datasource.model.MovieData
import ru.moviechecker.datasource.model.SeasonData
import ru.moviechecker.datasource.model.SiteData
import ru.moviechecker.datasource.model.SourceData
import ru.moviechecker.datasource.model.SourceDataEntry
import ru.moviechecker.datasource.model.StrictDataSource
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


// <title>Animedia Online - Смотреть аниме онлайн!</title>
private const val PATTERN_SITE_TITLE = "<title>(?<title>.*) -.*</title>"

// <a class="ftop-item d-flex has-overlay" href="/1593-moj-djejmon.html">
// <a class="ftop-item d-flex has-overlay" href="/2370-voennaja-hronika-malenkoj-devochki-2.html">
// <a class="ftop-item d-flex has-overlay" href="/2371-klevatess-2-korol-demonicheskih-zverej-i-legenda-o-lozhnom-geroe.html">
// <a class="ftop-item d-flex has-overlay" href="/2340-adskij-rezhim-gejmer-kotoryj-ljubit-spidran-stanovitsja-bespodobnym-v-parallelnom-mire-s-ustarevshimi-nastrojkami-2.html">
private const val PATTERN_EPISODE_LINK =
    """<a class="ftop-item d-flex has-overlay" href="(?<seasonPage>/(?<seasonPageId>\d+-(?<mnemonic>.*?))\.html)">"""

// <img src="/uploads/posts/2023-12/thumbs/fhk1xclgnqldcwyf__6895b8df64dcf260929c7c58a83a81e7.webp" alt="постер к аниме Мой Дэймон" >
private const val PATTERN_IMG_SRC = "<img src=\"(?<imgSrc>.*)\" alt=\".*\""

// <div class="ftop-item__title  line-clamp">Мой Дэймон </div>
// <div class="ftop-item__title  line-clamp">Старик из деревни становится Святым мечом 2 </div>
// <div class="ftop-item__title  line-clamp">Клеватесс 2: Король демонических зверей и легенда о ложном герое </div>
// <div class="ftop-item__title  line-clamp">Адский режим: Геймер, который любит спидран, становится бесподобным в параллельном мире с устаревшими настройками 2 </div>
private const val PATTERN_TITLE =
    """<div class="ftop-item__title +line-clamp">(?<movieTitle>.*?)\s*(?<seasonNumber>\d{1,2})?(?::\s*(?<seasonTitle>.+?))?\s*(?<seasonNumber2>\d{1,2})?\s*</div>"""

// <div class="ftop-item__meta poster__subtitle line-clamp">Сегодня, 17:13</div>
private const val PATTERN_DATE_TIME =
    "<div class=\"ftop-item__meta poster__subtitle line-clamp\">(?<date>.+)(?:, | <span>)(?<time>\\d{1,2}:\\d{1,2}|нестабильно)(?:</span>)?</div>"

// <div class="animseri"><span>13</span>серия</div>
private const val PATTERN_EPISODE_NNUMBER =
    "<div class=\"animseri\"><span>(?<episodeNumber>\\d+)?(?:-\\d+)?</span>серия</div>"

class AmediaDataSource : StrictDataSource("amedia", "https://amedia.online") {

    private val dateFormat = DateTimeFormatter.ofPattern("d-MM-yyyy")

    private val siteTitleRegex = PATTERN_SITE_TITLE.toRegex()
    private val episodeLinkRegex = PATTERN_EPISODE_LINK.toRegex()
    private val posterRegex = PATTERN_IMG_SRC.toRegex()
    private val titleRegex = PATTERN_TITLE.toRegex()
    private val dateTimeRegex = PATTERN_DATE_TIME.toRegex()
    private val episodeNumberRegex = PATTERN_EPISODE_NNUMBER.toRegex()

    override fun retrieveData(uri: URI): SourceData {
        val entries = mutableListOf<SourceDataEntry>()
        val lineIterator = readContent(uri).lines().iterator()
        val (siteTitle) = getFirstValueByRegex(lineIterator, siteTitleRegex)

        while (lineIterator.hasNext()) {
            try {
                val (seasonPage, seasonPageId, movieMnemonic) = getFirstValueByRegex(
                    lineIterator,
                    episodeLinkRegex
                )

                val (imgSrc) = getFirstValueByRegex(lineIterator, posterRegex)

                val (movieTitle, seasonNumber1, seasonTitle, seasonNumber2) = getFirstValueByRegex(lineIterator, titleRegex)
                val seasonNumber = seasonNumber1.ifBlank{ seasonNumber2 }

                val (dateString, timeString) = getFirstValueByRegex(lineIterator, dateTimeRegex)
                Log.d(this.javaClass.simpleName, "$movieTitle - $dateString $timeString")

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
                    // FIXME: На сайте запаздывает смена "сегодня" на "вчера" относительно UTC +3. Хотя само время стоит верно.
                    "Новая серия в", "Сегодня" -> LocalDate.now()

                    "Вчера" -> LocalDate.now().minusDays(1)

                    else -> LocalDate.parse(dateString, dateFormat)
                }
                val time =
                    if (timeString == "нестабильно") LocalTime.MIN else LocalTime.parse(timeString)
                val releaseTime = LocalDateTime.of(localDate, time)

                val (episodeNumber) = getFirstValueByRegex(lineIterator, episodeNumberRegex)

                val moviePageId = if (seasonNumber.isBlank()) movieMnemonic else movieMnemonic.substringBeforeLast("-$seasonNumber")
                val movie = MovieData(
                    pageId = moviePageId,
                    title = movieTitle
                )
                Log.d(this.javaClass.simpleName, "movie=$movie")

                val season = SeasonData(
                    number = seasonNumber.ifBlank { "1" }.toInt(),
                    title = if (seasonTitle.startsWith(movieTitle)) null else seasonTitle,
                    link = seasonPage,
                    posterLink = imgSrc
                )
                Log.d(this.javaClass.simpleName, "season=$season")

                val episode = EpisodeData(
                    number = episodeNumber.toInt(),
                    link = "/$seasonPageId/episode/$episodeNumber/seriya-onlayn.html",
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
                address = initialAddress
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