package moviechecker.lostfilm

import moviechecker.core.di.State
import moviechecker.core.di.datasource.DataRecord
import moviechecker.core.di.datasource.DataSource
import java.net.URI
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.Matcher

private const val baseURL = "https://www.lostfilmtv5.site"

class LostfilmDataSource : DataSource {

    private val newMovieClassRegex =
        "<a class=\"new-movie\" href=\"(?<href>.+)/\" title=\"(?<title>.+)\">".toRegex()
    private val todayClassRegex = "<td class=\"today\">".toRegex()
    private val expectedEpisodeRegex =
        "<a href=\"(?<href>.+)/\" class=\"title\">(?<title>.+)</br>".toRegex()
    private val breadcrumbsClassRegex = "<div class=\"breadcrumbs-pane\">".toRegex()
    private val movieItemClassRegex =
        "<a href=\"(?<moviePath>/series/(?<moviePageId>.+))/\" class=\"item\">(?<movieTitle>.+)</a>".toRegex()
    private val seasonItemClassRegex =
        "<a href=\"(?<seasonRef>.+)/\" class=\"item\"><div class=\"arrow\"></div>(?<seasonNumber>\\d+) сезон</a>".toRegex()
    private val episodeItemClassRegex =
        "<a href=\"(?<episodeRef>.+)/\" class=\"item\"><div class=\"arrow\"></div>(?<episodeNumber>\\d+) серия</a>".toRegex()
    private val seriaHeaderClassRegex = "<div class=\"seria-header\">".toRegex()
    private val thumbsClassRegex = "<img src=\"(?<posterRef>.+)\" class=\"thumb\" />".toRegex()
    private val titleRuClassRegex = "<h1 class=\"title-ru\">(?<episodeTitle>.+)</h1>".toRegex()
    private val titleEnClassRegex = "<div class=\"title-en\">(?<episodeTitle>.+)</div>".toRegex()
    private val expectedClassRegex = "<div class=\"expected\">Ожидается (?<date>.+)</div>".toRegex()
    private val dateRegex =
        "<span data-proper=\".+\" data-released=\"(?<dateReleased>\\d{2} .+ \\d{4})\">.+</span>".toRegex()

    private val dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("ru-RU"))


    override fun retrieveData(): Collection<DataRecord> {
        val baseUri = URI.create(baseURL)
        val recordList = mutableSetOf<DataRecord>()

        val lineIterator = URL(baseURL).readText().reader().buffered().readLines().iterator()
        while (lineIterator.hasNext()) {
            val inputLine = lineIterator.next()
            var href: String? = null
            newMovieClassRegex.find(inputLine)?.let { matchResult ->
                href = matchResult.groups["href"]?.value
            }
            todayClassRegex.find(inputLine)?.let {
                lineIterator.next() // skip tag
                expectedEpisodeRegex.find(lineIterator.next())?.let { matchResult ->
                    href = matchResult.groups["href"]?.value
                }
            }
            href?.let {
                ".+episode_\\d+".toRegex().find(it)?.let {
                    getEpisodeDetails(baseUri.resolve(href).toURL())?.let(recordList::add)
                }
            }
        }
        return recordList
    }

    private fun getEpisodeDetails(url: URL): DataRecord? {
        val dataRecordBuilder = DataRecord.Builder()
        dataRecordBuilder.site(URI.create(baseURL))
        dataRecordBuilder.episodeState(State.RELEASED)

        val lineIterator = url.readText().reader().buffered().readLines().iterator()
        while (lineIterator.hasNext()) {
            val inputLine = lineIterator.next()
            breadcrumbsClassRegex.find(inputLine)?.let {
                movieItemClassRegex.find(lineIterator.next())?.let { matchResult ->
                    val (_, moviePath, moviePageId, movieTitle) = matchResult.groupValues
                    dataRecordBuilder.moviePageId(moviePageId)
                        .movieLink(URI.create(moviePath))
                        .movieTitle(movieTitle)
                    lineIterator.next() // skip
                }
                seasonItemClassRegex.find(lineIterator.next())?.let { matchResult ->
                    val (_, seasonRef, seasonNumber) = matchResult.groupValues
                    dataRecordBuilder.seasonLink(URI.create(seasonRef))
                        .seasonNumber(seasonNumber.toInt())
                }
                episodeItemClassRegex.find(lineIterator.next())?.let { matchResult ->
                    val (_, episodeRef, episodeNumber) = matchResult.groupValues
                    dataRecordBuilder.episodeLink(URI.create(episodeRef))
                        .episodeNumber(episodeNumber.toInt())
                }
            }
            seriaHeaderClassRegex.find(inputLine)?.let {
                thumbsClassRegex.find(lineIterator.next())?.let { matchResult ->
                    matchResult.groups["posterRef"]?.let { matchGroup ->
                        dataRecordBuilder.moviePosterLink(URI.create(matchGroup.value))
                    }
                }
                titleRuClassRegex.find(lineIterator.next())?.let { matchResult ->
                    matchResult.groups["episodeTitle"]?.let { matchGroup ->
                        dataRecordBuilder.episodeTitle(matchGroup.value)
                    }
                }
                // is this data needed me?
                // titleEnClassRegex.find(lineIterator.next())?.let { matchResult->
                // }
            }
            expectedClassRegex.find(inputLine)?.let {
                dataRecordBuilder.episodeState(State.EXPECTED)
            }
            dateRegex.find(inputLine)?.let { matchResult ->
                matchResult.groups["dateReleased"]?.let { matchGroup ->
                    dataRecordBuilder.episodeDate(
                        LocalDateTime.of(
                            LocalDate.parse(matchGroup.value, dateFormat),
                            LocalTime.MIN
                        )
                    )
                }
                return dataRecordBuilder.build()
            }
        }
        throw Exception("No record produced for $url")
    }
}