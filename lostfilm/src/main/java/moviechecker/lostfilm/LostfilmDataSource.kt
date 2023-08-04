package moviechecker.lostfilm

import moviechecker.core.di.datasource.DataRecord
import moviechecker.core.di.datasource.DataSource
import moviechecker.core.di.State
import java.net.URI
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LostfilmDataSource : DataSource {

    private val BASE_URL = "https://www.lostfilmtv5.site"
    private val NEW_MOVIE_TAG_PATTERN =
        "<a class=\"new-movie\" href=\"(?<episodeRef>(?<seasonRef>(?<movieRef>/series/(?<moviePageId>.+))/.+)/.+)/\" title=\"(?<title>.+)\">".toRegex()
    private val EPISODE_TITLE_TAG_PATTERN = "<div class=\"title\">".toRegex()
    private val DATE_TAG_PATTERN = "<div class=\"date\">(?<date>.+)</div>".toRegex()
    private val IMG_TAG_PATTERN = "<img src=\"(?<posterLink>.+)\" />".toRegex()
    private val ID_PATTERN =
        "(?<seasonNumber>\\d+) сезон (?<episodeTitle>(?<episodeNumber>\\d+) серия)".toRegex()
    private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override fun retrieveData(): Collection<DataRecord> {
        val dataList = mutableListOf<DataRecord>()
        val dataRecordBuilder = DataRecord.Builder()
        dataRecordBuilder.site(URI.create(BASE_URL))
        dataRecordBuilder.episodeState(State.RELEASED)

        val lineIterator = URL(BASE_URL).readText().reader().buffered().readLines().iterator()
        while (lineIterator.hasNext()) {
            NEW_MOVIE_TAG_PATTERN.find(lineIterator.next())?.let { matchResult ->
                operator fun <T> List<T>.component6(): T = get(5)
                val (_, episodeRef, seasonRef, movieRef, moviePageId, title) = matchResult.groupValues
                dataRecordBuilder
                    .moviePageId(moviePageId)
                    .movieTitle(title)
                    .movieLink(URI.create(movieRef))
                    .seasonLink(URI.create(seasonRef))
                    .episodeLink(URI.create(episodeRef))

                EPISODE_TITLE_TAG_PATTERN.find(lineIterator.next())?.let { _ ->
                    ID_PATTERN.find(lineIterator.next())?.let { idMatcherResult ->
                        val (_, seasonNumber, episodeTitle, episodeNumber) = idMatcherResult.groupValues
                        dataRecordBuilder
                            .seasonNumber(seasonNumber.toInt())
                            .episodeNumber(episodeNumber.toInt())
                            .episodeTitle(episodeTitle)
                        lineIterator.next() // skip closing tag line
                    }
                }

                DATE_TAG_PATTERN.find(lineIterator.next())?.let { matchResult ->
                    val (_, date) = matchResult.groupValues
                    dataRecordBuilder.episodeDate(
                        LocalDateTime.of(
                            LocalDate.parse(date, dateFormat),
                            LocalTime.MIN
                        )
                    )
                }

                IMG_TAG_PATTERN.find(lineIterator.next())?.let { matchResult ->
                    val (_, posterLink) = matchResult.groupValues
                    dataRecordBuilder.moviePosterLink(URI.create(posterLink))
                }

                dataList.add(dataRecordBuilder.build())
            }
        }

        return dataList
    }
}