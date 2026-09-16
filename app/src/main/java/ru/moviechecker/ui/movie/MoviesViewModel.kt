package ru.moviechecker.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.moviechecker.checkerApplication
import ru.moviechecker.database.episode.EpisodeEntity
import ru.moviechecker.database.episode.EpisodeRepository
import ru.moviechecker.database.episode.EpisodeState
import ru.moviechecker.database.movie.ExpectedCard
import ru.moviechecker.database.movie.ExpectedCardEpisode
import ru.moviechecker.database.movie.ExpectedCardSeason
import ru.moviechecker.database.movie.MovieCard
import ru.moviechecker.database.movie.MovieCardEpisode
import ru.moviechecker.database.movie.MovieCardSeason
import ru.moviechecker.database.movie.MovieDetails
import ru.moviechecker.database.movie.MovieRepository
import ru.moviechecker.database.season.SeasonEntity
import ru.moviechecker.model.Identifiable
import java.net.URI
import java.time.LocalDateTime

class MoviesViewModel(
    private val movieRepository: MovieRepository,
    private val episodeRepository: EpisodeRepository
) : ViewModel() {

    var novelties = movieRepository.getNoveltiesStream()
        .map { it.map(MovieCardModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    val expected = movieRepository.getExpectedStream()
        .map {
            it.filter { item -> item.episode.date > LocalDateTime.now() }
                .map(MovieCardModel::fromEntity)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    val movies = movieRepository.getMovieCardStream()
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
                MovieDetailsCardModel.fromEntity(movieRepository.getMovieDetails(movieId))
        }
    }

    fun update(id: Int, kinopoiskId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.updateKinopoiskId(id, kinopoiskId)
        }
    }

    fun toggleFavoritesMark(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.toggleFavoritesMark(movieId)
        }
    }

    fun markEpisodeViewed(episodeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            episodeRepository.updateEpisodeState(episodeId, EpisodeState.VIEWED)
        }
    }

    fun toggleEpisodeViewedMark(episodeId: Int, isViewed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newState = if (isViewed) EpisodeState.RELEASED else EpisodeState.VIEWED
            episodeRepository.updateEpisodeState(episodeId, newState)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                MoviesViewModel(
                    checkerApplication().container.movieRepository,
                    checkerApplication().container.episodeRepository
                )
            }
        }
    }
}

data class MovieCardModel(
    override val id: Int,
    val title: String,
    val poster: ByteArray? = null,
    val favoritesMark: Boolean,
    val kinopoiskId: String? = null,
    val season: SeasonModel,
    val episode: EpisodeModel,
    val hasMoreEpisodes: Boolean,
    val updatedAt: LocalDateTime
) : Identifiable {
    companion object Factory {

        fun fromEntity(entity: MovieCard): MovieCardModel {
            val host = if (entity.site.useMirror) entity.site.mirror else entity.site.address
            val season = SeasonModel.fromEntity(entity.season)
            val episode =
                EpisodeModel.fromEntity(entity.episode, URI.create("${host}${entity.episode.link}"))
            return MovieCardModel(
                id = entity.id,
                title = entity.title,
                poster = entity.poster,
                favoritesMark = entity.favoritesMark,
                kinopoiskId = entity.kinopoiskId,
                season = season,
                episode = episode,
                hasMoreEpisodes = entity.episode.number < entity.season.lastEpisodeNumber,
                updatedAt = entity.season.lastEpisodeDate
            )
        }

        fun fromEntity(entity: ExpectedCard): MovieCardModel {
            val host = if (entity.site.useMirror) entity.site.mirror else entity.site.address
            val season = SeasonModel.fromEntity(entity.season)
            val episode =
                EpisodeModel.fromEntity(entity.episode, URI.create("${host}${entity.episode.link}"))
            return MovieCardModel(
                id = entity.id,
                title = entity.title,
                poster = entity.poster,
                favoritesMark = entity.favoritesMark,
                kinopoiskId = entity.kinopoiskId,
                season = season,
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
        if (hasMoreEpisodes != other.hasMoreEpisodes) return false
        if (title != other.title) return false
        if (!poster.contentEquals(other.poster)) return false
        if (kinopoiskId != other.kinopoiskId) return false
        if (season != other.season) return false
        if (episode != other.episode) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + favoritesMark.hashCode()
        result = 31 * result + hasMoreEpisodes.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + (kinopoiskId?.hashCode() ?: 0)
        result = 31 * result + season.hashCode()
        result = 31 * result + episode.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }

}

data class SeasonModel(
    val number: Int,
    val title: String? = null
) {
    companion object Factory {
        fun fromEntity(entity: MovieCardSeason): SeasonModel {
            return SeasonModel(
                number = entity.number,
                title = entity.title
            )
        }

        fun fromEntity(entity: ExpectedCardSeason): SeasonModel {
            return SeasonModel(
                number = entity.number,
                title = entity.title
            )
        }
    }
}

data class EpisodeModel(
    override val id: Int,
    val number: Int,
    val title: String? = null,
    val link: URI,
    val date: LocalDateTime,
    val viewedMark: Boolean
) : Identifiable {
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
    override val id: Int,
    val siteId: Int,
    val pageId: String,
    val title: String,
    val link: String? = null,
    val poster: ByteArray? = null,
    val favoritesMark: Boolean = false,
    val kinopoiskId: String? = null,
    val seasons: List<SeasonCardModel>
) : Identifiable {
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
                kinopoiskId = entity.kinopoiskId,
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
        if (!poster.contentEquals(other.poster)) return false
        if (kinopoiskId != other.kinopoiskId) return false
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
        result = 31 * result + (poster?.contentHashCode() ?: 0)
        result = 31 * result + (kinopoiskId?.hashCode() ?: 0)
        result = 31 * result + seasons.hashCode()
        return result
    }

}

data class SeasonCardModel(
    override val id: Int,
    val number: Int,
    var title: String? = null,
    var link: String? = null,
    var poster: ByteArray? = null,
    val episodes: List<EpisodeCardModel>
) : Identifiable {
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
        if (!poster.contentEquals(other.poster)) return false
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