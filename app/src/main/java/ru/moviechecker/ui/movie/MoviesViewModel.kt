package ru.moviechecker.ui.movie

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.moviechecker.MoviesRoute
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.MovieCard2
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.database.sites.SiteEntity
import ru.moviechecker.database.sites.SitesRepository
import ru.moviechecker.workers.AsyncRetrieveDataWorker
import java.net.URI
import java.time.LocalDateTime

class MovieCardsViewModel(
    savedStateHandle: SavedStateHandle,
    private val sitesRepository: SitesRepository,
    private val moviesRepository: MoviesRepository,
    private val episodesRepository: EpisodesRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MoviesRoute>()

    private val _isLoading = MutableStateFlow(false)
    private val _viewModelState: MutableStateFlow<MoviesUiState>
        get() = MutableStateFlow(
            MoviesUiState(
                shouldShowOnlyFavorites = false,
                shouldShowViewedEpisodes = true
            )
        )

    val uiState = _viewModelState
        .combine(_isLoading) { state, isLoading ->
            MoviesUiState(
                shouldShowViewedEpisodes = state.shouldShowViewedEpisodes,
                shouldShowOnlyFavorites = state.shouldShowOnlyFavorites,
                isLoading = isLoading
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _viewModelState.value
        )

    // FIXME: какая-то фигня - переделать
    val site = if (route.siteId != null) sitesRepository.getByIdStream(id = route.siteId)
        .map { site -> SiteModel.fromEntity(site) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SiteModel(title = null)
        ) else flowOf<SiteModel>().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SiteModel(title = null)
    )

    val movies = moviesRepository.getMovieCardsStream(siteId = route.siteId)
        .map { it.map(MovieCardModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    fun onRefresh(context: Context) {
        _isLoading.update { true }

        val workManager = WorkManager.getInstance(context)
        val workRequest = OneTimeWorkRequestBuilder<AsyncRetrieveDataWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()
        workManager
            .beginUniqueWork(
                uniqueWorkName = "Проверка новых релизов",
                existingWorkPolicy = ExistingWorkPolicy.KEEP,
                request = workRequest
            )
            .enqueue()

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(workRequest.id)
                .collect { workInfo ->
                    Log.d(this.javaClass.simpleName, "Получили статус обновления: ${workInfo?.state}")
                    if (workInfo?.state?.isFinished == true) {
                        Log.d(this.javaClass.simpleName, "Обновление закончено")
                        _isLoading.update { false }
                    }
                }
        }
    }

    fun toggleFavoritesMark(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.findById(movieId)?.let { movie ->
                movie.favoritesMark = !movie.favoritesMark
                moviesRepository.updateMovie(movie)
            }
        }
    }

    fun markEpisodeViewed(episodeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            episodesRepository.findById(episodeId)?.let { episode ->
                episode.state = EpisodeState.VIEWED
                episodesRepository.updateEpisode(episode)
            }
        }
    }

    fun toggleEpisodeViewedMark(episodeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            episodesRepository.findById(episodeId)?.let { episode ->
                episode.state = if (episode.state == EpisodeState.VIEWED) {
                    EpisodeState.RELEASED
                } else if (episode.state == EpisodeState.RELEASED) {
                    EpisodeState.VIEWED
                } else {
                    episode.state
                }
                episodesRepository.updateEpisode(episode)
            }
        }
    }

    fun toggleShouldShowOnlyFavoritesFlag() {
        _viewModelState.update {
            it.copy(
                shouldShowOnlyFavorites = !it.shouldShowOnlyFavorites
            )
        }
    }

    fun toggleShouldShowViewedEpisodesFlag() {
        _viewModelState.update {
            it.copy(
                shouldShowViewedEpisodes = !it.shouldShowViewedEpisodes
            )
        }
    }

    companion object {
        fun provideFactory(
            savedStateHandle: SavedStateHandle,
            sitesRepository: SitesRepository,
            moviesRepository: MoviesRepository,
            episodesRepository: EpisodesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieCardsViewModel(
                    savedStateHandle,
                    sitesRepository,
                    moviesRepository,
                    episodesRepository
                ) as T
            }
        }
    }
}

data class MoviesUiState(
    val shouldShowOnlyFavorites: Boolean = false,
    val shouldShowViewedEpisodes: Boolean = true,
    val isLoading: Boolean = false
)

data class SiteModel(
    val title: String?
) {
    companion object Factory {

        fun fromEntity(entity: SiteEntity): SiteModel {
            return SiteModel(entity.title)
        }
    }
}

data class MovieCardModel(
    val id: Int,
    val title: String,
    val poster: ByteArray? = null,
    val favoritesMark: Boolean,
    val viewedMark: Boolean,
    val seasonId: Int,
    val seasonNumber: Int,
    val nextEpisodeId: Int? = null,
    val nextEpisodeNumber: Int? = null,
    val nextEpisodeTitle: String? = null,
    val nextEpisodeLink: URI? = null,
    val nextEpisodeDate: LocalDateTime? = null,
    val lastEpisodeId: Int,
    val lastEpisodeNumber: Int,
    val lastEpisodeTitle: String?,
    val lastEpisodeLink: URI,
    val lastEpisodeDate: LocalDateTime
) {
    companion object Factory {

        fun fromEntity(entity: MovieCard2): MovieCardModel {
            return MovieCardModel(
                id = entity.id,
                title = entity.title,
                poster = entity.poster,
                favoritesMark = entity.favoritesMark,
                viewedMark = entity.viewedMark,
                seasonId = 0, //entity.seasonId,
                seasonNumber = entity.seasonNumber,
                nextEpisodeId = entity.nextEpisodeId,
                nextEpisodeNumber = entity.nextEpisodeNumber,
                nextEpisodeTitle = entity.nextEpisodeTitle,
                nextEpisodeLink = entity.nextEpisodeLink,
                nextEpisodeDate = entity.nextEpisodeDate,
                lastEpisodeId = entity.lastEpisodeId,
                lastEpisodeNumber = entity.lastEpisodeNumber,
                lastEpisodeTitle = entity.lastEpisodeTitle,
                lastEpisodeLink = entity.lastEpisodeLink,
                lastEpisodeDate = entity.lastEpisodeDate
            )
        }
    }
}
