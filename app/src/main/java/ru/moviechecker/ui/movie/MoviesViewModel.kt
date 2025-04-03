package ru.moviechecker.ui.movie

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.MovieCardsView
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.workers.AsyncRetrieveDataWorker
import java.net.URI
import java.time.LocalDateTime

class MovieCardsViewModel(
    private val moviesRepository: MoviesRepository,
    private val episodesRepository: EpisodesRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(
        MoviesUiState(
            shouldShowOnlyFavorites = false,
            shouldShowViewedEpisodes = true
        )
    )

    val uiState = viewModelState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = viewModelState.value
        )

    val movies = moviesRepository.getMovieCardsStream()
        .map { it.map(MovieCardModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    fun onRefresh(context: Context) {
        viewModelState.update { it.copy(isLoading = true) }

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
                    Log.d(this.javaClass.simpleName, "Статус обновления: ${workInfo?.state}")
                    if (workInfo?.state?.isFinished == true) {
                        viewModelState.update { it.copy(isLoading = false) }
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
        viewModelState.update {
            it.copy(
                shouldShowOnlyFavorites = !it.shouldShowOnlyFavorites
            )
        }
    }

    fun toggleShouldShowViewedEpisodesFlag() {
        viewModelState.update {
            it.copy(
                shouldShowViewedEpisodes = !it.shouldShowViewedEpisodes
            )
        }
    }

    companion object {
        fun provideFactory(
            moviesRepository: MoviesRepository,
            episodesRepository: EpisodesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MovieCardsViewModel(moviesRepository, episodesRepository) as T
            }
        }
    }
}

data class MoviesUiState(
    val shouldShowOnlyFavorites: Boolean = false,
    val shouldShowViewedEpisodes: Boolean = true,
    val isLoading: Boolean = false
)

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

        fun fromEntity(entity: MovieCardsView): MovieCardModel {
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
