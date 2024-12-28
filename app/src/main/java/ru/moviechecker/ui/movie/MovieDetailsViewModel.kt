package ru.moviechecker.ui.movie

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.MovieEntity
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.database.seasons.SeasonEntity
import ru.moviechecker.database.seasons.SeasonsRepository
import java.net.URI
import java.time.LocalDateTime

class MovieDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val moviesRepository: MoviesRepository,
    private val seasonsRepository: SeasonsRepository,
    private val episodesRepository: EpisodesRepository
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle[MovieDetailsDestination.movieIdArg])

    @OptIn(ExperimentalCoroutinesApi::class)
    val movieUiState: StateFlow<MovieDetailsUiState> =
        moviesRepository.getMovieByIdStream(movieId)
            .filterNotNull()
            .map(MovieDetails::toDetails)
            .flatMapConcat { movie ->
                seasonsRepository.getSeasonsByMovieIdStream(movie.id)
                    .filterNotNull()
                    .flatMapConcat { seasons ->
                        episodesRepository.getEpisodesBySeasonIdStream(seasons.map { season -> season.id })
                            .filterNotNull()
                            .map { episodes ->
                                val episodeMap = episodes.groupBy(EpisodeEntity::seasonId)
                                seasons.map { season ->
                                    SeasonModel.create(
                                        entity = season,
                                        episodes = episodeMap.getValue(season.id)
                                            .map(EpisodeModel::fromEntity)
                                    )
                                }
                            }
                    }
                    .map { seasons -> MovieDetailsUiState(movie = movie, seasons = seasons) }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = MovieDetailsUiState.empty
            )

}

data class MovieDetailsUiState(
    val movie: MovieDetails,
    val seasons: List<SeasonModel>,
    val refreshing: Boolean = false
) {
    companion object {
        val empty = MovieDetailsUiState(
            movie = MovieDetails.empty,
            seasons = listOf()
        )
    }
}

data class MovieDetails(
    val id: Int = 0,
    val siteId: Int = 0,
    val site: URI,
    val pageId: String,
    val title: String,
    val link: String? = null,
    val poster: ByteArray? = null,
    val favoritesMark: Boolean
) {

    companion object Factory {

        val empty = MovieDetails(
            site = URI.create(""),
            pageId = "",
            title = "",
            link = null,
            poster = null,
            favoritesMark = false
        )

        fun toDetails(entity: MovieEntity): MovieDetails {
            return MovieDetails(
                id = entity.id,
                siteId = entity.siteId,
                site = URI.create(""),
                pageId = entity.pageId,
                title = entity.title,
                link = entity.link,
                poster = entity.poster,
                favoritesMark = entity.favoritesMark
            )
        }

        fun toEntity(details: MovieDetails): MovieEntity {
            return MovieEntity(
                id = details.id,
                siteId = details.siteId,
                pageId = details.pageId,
                title = details.title,
                link = details.link,
                poster = details.poster,
                favoritesMark = details.favoritesMark
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieDetails

        if (siteId != other.siteId) return false
        if (pageId != other.pageId) return false
        if (title != other.title) return false
        if (link != other.link) return false
        if (poster != null) {
            if (other.poster == null) return false
            if (!poster.contentEquals(other.poster)) return false
        } else if (other.poster != null) return false
        if (favoritesMark != other.favoritesMark) return false

        return true
    }

    override fun hashCode(): Int {
        var result = site.hashCode()
        result = 31 * result + pageId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (link?.hashCode() ?: 0)
        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + favoritesMark.hashCode()
        return result
    }

}

data class SeasonModel(
    val id: Int = 0,
    val number: Int,
    val title: String? = null,
    val link: String? = null,
    val poster: ByteArray? = null,
    val episodes: List<EpisodeModel> = listOf()
) {
    companion object Factory {
        fun create(entity: SeasonEntity, episodes: List<EpisodeModel>): SeasonModel {
            return SeasonModel(
                id = entity.id,
                number = entity.number,
                title = entity.title,
                link = entity.link,
                poster = entity.poster,
                episodes = episodes
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeasonModel

        if (number != other.number) return false
        if (title != other.title) return false
        if (link != other.link) return false
        if (poster != null) {
            if (other.poster == null) return false
            if (!poster.contentEquals(other.poster)) return false
        } else if (other.poster != null) return false
        if (episodes != other.episodes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = number
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (link?.hashCode() ?: 0)
        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + episodes.hashCode()
        return result
    }
}

data class EpisodeModel(
    val id: Int = 0,
    val number: Int,
    val title: String? = null,
    val link: String,
    val state: EpisodeState,
    val date: LocalDateTime
) {
    companion object Factory {
        fun fromEntity(entity: EpisodeEntity): EpisodeModel {
            return EpisodeModel(
                id = entity.id,
                number = entity.number,
                title = entity.title,
                link = entity.link,
                state = entity.state,
                date = entity.date
            )
        }
    }

}
