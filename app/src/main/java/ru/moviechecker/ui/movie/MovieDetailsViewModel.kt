package ru.moviechecker.ui.movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.MovieDetails
import ru.moviechecker.database.movies.MovieEntity
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.database.seasons.SeasonWithEpisodes
import ru.moviechecker.database.seasons.SeasonsRepository
import java.net.URI
import java.time.LocalDateTime

class MovieDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val moviesRepository: MoviesRepository,
    private val seasonsRepository: SeasonsRepository,
    private val episodesRepository: EpisodesRepository
) : ViewModel() {

    val movieId: Int = checkNotNull(savedStateHandle[MovieDetailsDestination.ID_ARG])

    val movieDataUiState: StateFlow<MovieDataUiState> =
        moviesRepository.getMovieDetailsStream(movieId)
            .map(MovieModel::fromEntity)
            .map { model -> MovieDataUiState(model) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = MovieDataUiState(MovieModel.empty)
            )

    val seasonListUiState: StateFlow<SeasonListUiState> =
        seasonsRepository.getSeasonsWithEpisodesByMovieIdStream(movieId)
            .map { seasons ->
                seasons.asFlow()
                    .map { season ->
                        SeasonModel.fromEntity(
                            entity = season,
                            episodes = season.episodes.asFlow().map(EpisodeModel::fromEntity)
                                .toList()
                        )
                    }
                    .toList()
            }
            .map { models -> SeasonListUiState(models) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = SeasonListUiState(listOf())
            )

    val episodeListUiState: StateFlow<EpisodeListUiState> =
        episodesRepository.getEpisodesBySeasonIdStream(seasonListUiState.value.expandedSeasonId)
            .map { episodes ->
                EpisodeListUiState(
                    episodes.asFlow().map { episode -> EpisodeModel.fromEntity(episode) }.toList()
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = EpisodeListUiState(listOf())
            )


    fun switchFavoritesMark(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.findById(movieId)?.let { movie ->
                movie.favoritesMark = !movie.favoritesMark
                moviesRepository.updateMovie(movie)
            }
        }
    }

    fun switchEpisodeViewedMark(episodeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            episodesRepository.findById(episodeId)?.let { episode ->
                episode.state = if (episode.state == EpisodeState.VIEWED) {
                    // TODO: Найти способ не потерять состояние EXPECTED. Например статус просмотра вынести в отдельное поле.
                    EpisodeState.RELEASED
                } else {
                    EpisodeState.VIEWED
                }
                episodesRepository.updateEpisode(episode)
            }
        }
    }
}

data class MovieDataUiState(
    val movie: MovieModel
)

data class SeasonListUiState(
    val seasons: List<SeasonModel>,
    var expandedSeasonId: Int = 0
)

data class EpisodeListUiState(
    val episodes: List<EpisodeModel>
)

data class MovieModel(
    val id: Int = 0,
    val site: URI,
    val pageId: String,
    val title: String,
    val link: String? = null,
    val poster: ByteArray? = null,
    val favoritesMark: Boolean,
    val seasonCount: Int
) {
    companion object Factory {

        val empty = MovieModel(
            id = 0,
            site = URI.create(""),
            pageId = "",
            title = "",
            link = "",
            poster = byteArrayOf(),
            favoritesMark = false,
            seasonCount = 0
        )

        fun fromEntity(entity: MovieDetails): MovieModel {
            return MovieModel(
                id = entity.id,
                site = entity.siteAddress,
                pageId = entity.pageId,
                title = entity.title,
                link = entity.link,
                poster = entity.poster,
                favoritesMark = entity.favoritesMark,
                seasonCount = entity.seasonCount
            )
        }

        fun toEntity(details: MovieModel): MovieEntity {
            return MovieEntity(
                id = details.id,
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

        other as MovieModel

        if (id != other.id) return false
        if (site != other.site) return false
        if (pageId != other.pageId) return false
        if (title != other.title) return false
        if (link != other.link) return false
        if (poster != null) {
            if (other.poster == null) return false
            if (!poster.contentEquals(other.poster)) return false
        } else if (other.poster != null) return false
        if (favoritesMark != other.favoritesMark) return false
        if (seasonCount != other.seasonCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + site.hashCode()
        result = 31 * result + pageId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (link?.hashCode() ?: 0)
        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + favoritesMark.hashCode()
        result = 31 * result + seasonCount
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
        fun fromEntity(entity: SeasonWithEpisodes, episodes: List<EpisodeModel>): SeasonModel {
            return SeasonModel(
                id = entity.seasonEntity.id,
                number = entity.seasonEntity.number,
                title = entity.seasonEntity.title,
                link = entity.seasonEntity.link,
                poster = entity.seasonEntity.poster,
                episodes = episodes
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeasonModel

        if (id != other.id) return false
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
        var result = id
        result = 31 * result + number
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
