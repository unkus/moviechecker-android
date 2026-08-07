package ru.moviechecker.ui.movie

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.ExpectedCard
import ru.moviechecker.database.movies.ExpectedCardEpisode
import ru.moviechecker.database.movies.MovieCard
import ru.moviechecker.database.movies.MovieCardEpisode
import ru.moviechecker.database.movies.MovieDetails
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.database.seasons.SeasonEntity
import java.net.URI
import java.time.LocalDateTime
import kotlin.collections.map

class MoviesViewModel(
    private val moviesRepository: MoviesRepository,
    private val episodesRepository: EpisodesRepository
) : ViewModel() {

    var newReleases = moviesRepository.getNewReleasesStream()
        .map { it.map(MovieCardModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    val expected = moviesRepository.getExpectedStream()
        .map { it.map(MovieCardModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    val movies = moviesRepository.getMovieCardStream()
        .map { it.map(MovieCardModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val _movieDetails = MutableStateFlow<MovieDetailsCardModel?>(null)
    val movieDetails: StateFlow<MovieDetailsCardModel?> = _movieDetails

    fun loadDetails(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _movieDetails.value =
                MovieDetailsCardModel.fromEntity(moviesRepository.getMovieDetails(movieId))
        }
    }

    fun toggleFavoritesMark(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.toggleFavoritesMark(movieId)
        }
    }

    fun markEpisodeViewed(episodeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            episodesRepository.updateEpisodeState(episodeId, EpisodeState.VIEWED)
        }
    }

    fun toggleEpisodeViewedMark(episodeId: Int, isViewed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newState = if (isViewed) EpisodeState.RELEASED else EpisodeState.VIEWED
            episodesRepository.updateEpisodeState(episodeId, newState)
        }
    }

    companion object {
        fun provideFactory(
            moviesRepository: MoviesRepository,
            episodesRepository: EpisodesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MoviesViewModel(
                    moviesRepository = moviesRepository,
                    episodesRepository = episodesRepository
                ) as T
            }
        }
    }
}

data class MovieCardModel(
    val id: Int,
    val title: String,
    val poster: ByteArray? = null,
    val favoritesMark: Boolean,
    val seasonNumber: Int,
    val episode: EpisodeModel,
    val hasMoreEpisodes: Boolean,
    val updatedAt: LocalDateTime
) {
    companion object Factory {

        fun fromEntity(entity: MovieCard): MovieCardModel {
            val host = if (entity.site.useMirror) entity.site.mirror else entity.site.address
            val episode =
                EpisodeModel.fromEntity(entity.episode, URI.create("${host}${entity.episode.link}"))
            return MovieCardModel(
                id = entity.id,
                title = entity.title,
                poster = entity.poster,
                favoritesMark = entity.favoritesMark,
                seasonNumber = entity.season.number,
                episode = episode,
                hasMoreEpisodes = entity.episode.number < entity.season.lastEpisodeNumber,
                updatedAt = entity.season.lastEpisodeDate
            )
        }

        fun fromEntity(entity: ExpectedCard): MovieCardModel {
            val host = if (entity.site.useMirror) entity.site.mirror else entity.site.address
            val episode =
                EpisodeModel.fromEntity(entity.episode, URI.create("${host}${entity.episode.link}"))
            return MovieCardModel(
                id = entity.id,
                title = entity.title,
                poster = entity.poster,
                favoritesMark = entity.favoritesMark,
                seasonNumber = entity.season.number,
                episode = episode,
                hasMoreEpisodes = false,
                updatedAt = entity.episode.date
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieCardModel

        if (id != other.id) return false
        if (favoritesMark != other.favoritesMark) return false
        if (seasonNumber != other.seasonNumber) return false
        if (hasMoreEpisodes != other.hasMoreEpisodes) return false
        if (title != other.title) return false
//        if (!poster.contentEquals(other.poster)) return false
        if (episode != other.episode) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + favoritesMark.hashCode()
        result = 31 * result + seasonNumber
        result = 31 * result + hasMoreEpisodes.hashCode()
        result = 31 * result + title.hashCode()
//        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + episode.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}

data class EpisodeModel(
    val id: Int,
    val number: Int,
    val title: String? = null,
    val link: URI,
    val date: LocalDateTime,
    val viewedMark: Boolean
) {
    companion object Factory {
        fun fromEntity(entity: MovieCardEpisode, link: URI): EpisodeModel {
            return EpisodeModel(
                id = entity.id,
                number = entity.number,
                title = entity.title,
                link = link,
                date = entity.date,
                viewedMark = entity.viewedMark
            )
        }

        fun fromEntity(entity: ExpectedCardEpisode, link: URI): EpisodeModel {
            return EpisodeModel(
                id = entity.id,
                number = entity.number,
                title = entity.title,
                link = link,
                date = entity.date,
                viewedMark = false
            )
        }
    }
}

data class MovieDetailsCardModel(
    val id: Int,
    val siteId: Int,
    val pageId: String,
    val title: String,
    val link: String? = null,
    val poster: ByteArray? = null,
    val favoritesMark: Boolean = false,
    val seasons: List<SeasonCardModel>
) {
    companion object Factory {

        fun fromEntity(
            entity: MovieDetails
        ): MovieDetailsCardModel {
            return MovieDetailsCardModel(
                id = entity.id,
                siteId = entity.siteId,
                pageId = entity.pageId,
                title = entity.title,
                link = entity.link,
                poster = entity.poster,
                favoritesMark = entity.favoritesMark,
                seasons = entity.seasons.map { (season, episodes) ->
                    SeasonCardModel.fromEntity(season, episodes.map(EpisodeCardModel::fromEntity))
                }
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieDetailsCardModel

        if (id != other.id) return false
        if (siteId != other.siteId) return false
        if (favoritesMark != other.favoritesMark) return false
        if (pageId != other.pageId) return false
        if (title != other.title) return false
        if (link != other.link) return false
//        if (!poster.contentEquals(other.poster)) return false
        if (seasons != other.seasons) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + siteId
        result = 31 * result + favoritesMark.hashCode()
        result = 31 * result + pageId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (link?.hashCode() ?: 0)
//        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + seasons.hashCode()
        return result
    }
}

data class SeasonCardModel(
    val id: Int,
    val number: Int,
    var title: String? = null,
    var link: String? = null,
    var poster: ByteArray? = null,
    val episodes: List<EpisodeCardModel>
) {
    companion object Factory {
        fun fromEntity(
            entity: SeasonEntity,
            episodes: List<EpisodeCardModel> = listOf()
        ): SeasonCardModel {
            return SeasonCardModel(
                id = entity.id,
                number = entity.number,
                title = entity.title,
                poster = entity.poster,
                episodes = episodes
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeasonCardModel

        if (id != other.id) return false
        if (number != other.number) return false
        if (title != other.title) return false
        if (link != other.link) return false
//        if (!poster.contentEquals(other.poster)) return false
        if (episodes != other.episodes) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + number
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (link?.hashCode() ?: 0)
//        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + episodes.hashCode()
        return result
    }
}

data class EpisodeCardModel(
    val id: Int = 0,
    val number: Int,
    var title: String? = null,
    var link: String,
    var date: LocalDateTime,
    var viewedMark: Boolean
) {
    companion object Factory {
        fun fromEntity(entity: EpisodeEntity): EpisodeCardModel {
            return EpisodeCardModel(
                id = entity.id,
                number = entity.number,
                title = entity.title,
                link = entity.link,
                date = entity.date,
                viewedMark = entity.state == EpisodeState.VIEWED
            )
        }
    }
}