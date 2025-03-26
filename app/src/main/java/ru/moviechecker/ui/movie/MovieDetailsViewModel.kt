package ru.moviechecker.ui.movie

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.moviechecker.MovieDetailsRoute
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.MovieDetails
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.database.seasons.SeasonWithEpisodes
import ru.moviechecker.database.seasons.SeasonsRepository
import java.time.LocalDateTime

class MovieDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val moviesRepository: MoviesRepository,
    private val seasonsRepository: SeasonsRepository,
    private val episodesRepository: EpisodesRepository
) : ViewModel() {

    private val movieRoute = savedStateHandle.toRoute<MovieDetailsRoute>()

    private val viewModelState = MutableStateFlow(
        MovieDetailsUiState(
            movie = MovieModel.empty,
            expandedSeasonNumber = 0
        )
    )

    val uiState = viewModelState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value
        )

    val seasons = seasonsRepository.getSeasonsWithEpisodesByMovieIdStream(movieRoute.id)
        .map { entities -> entities.map(SeasonModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            moviesRepository.getMovieWithSiteByIdStream(movieRoute.id)
                .collect { entity ->
                    viewModelState.update {
                        it.copy(
                            movie = MovieModel.fromEntity(entity)
                        )
                    }
                }
        }
    }

    fun onRefresh(context: Context) {

    }

    fun setExpandedSeason(seasonNumber: Int) {
        viewModelState.update { it.copy(expandedSeasonNumber = if (it.expandedSeasonNumber == seasonNumber) 0 else seasonNumber) }
    }

    fun toggleFavoritesMark(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.findById(movieId)?.let { movie ->
                movie.favoritesMark = !movie.favoritesMark
                moviesRepository.updateMovie(movie)
            }
        }
    }

    fun toggleEpisodeViewedMark(episodeId: Int) {
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

    companion object {
        fun provideFactory(
            savedStateHandle: SavedStateHandle,
            moviesRepository: MoviesRepository,
            seasonsRepository: SeasonsRepository,
            episodesRepository: EpisodesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(
                    savedStateHandle,
                    moviesRepository,
                    seasonsRepository,
                    episodesRepository
                ) as T
            }
        }
    }
}

data class MovieDetailsUiState(
    val movie: MovieModel,
    var expandedSeasonNumber: Int = 0
)

data class MovieModel(
    val id: Int,
    val site: String,
    val pageId: String,
    val title: String,
    val link: String? = null,
    val poster: ByteArray? = null,
    val favoritesMark: Boolean
) {
    companion object Factory {

        val empty = MovieModel(
            id = 0,
            site = "",
            pageId = "",
            title = "",
            link = "",
            poster = byteArrayOf(),
            favoritesMark = false
        )

        fun fromEntity(entity: MovieDetails): MovieModel {
            return MovieModel(
                id = entity.id,
                site = entity.address,
                pageId = entity.pageId,
                title = entity.title,
                link = entity.link,
                poster = entity.poster,
                favoritesMark = entity.favoritesMark
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
        return result
    }
}

data class SeasonModel(
    val id: Int,
    val number: Int,
    val title: String? = null,
    val link: String? = null,
    val poster: ByteArray? = null,
    val episodes: List<EpisodeModel> = emptyList()
) {
    companion object Factory {
        fun fromEntity(entity: SeasonWithEpisodes): SeasonModel {
            return SeasonModel(
                id = entity.season.id,
                number = entity.season.number,
                title = entity.season.title,
                link = entity.season.link,
                poster = entity.season.poster,
                episodes = entity.episodes.map(EpisodeModel::fromEntity)
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
    val id: Int,
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
