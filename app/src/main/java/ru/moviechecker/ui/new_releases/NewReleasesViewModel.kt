package ru.moviechecker.ui.new_releases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.MoviesRepository
import ru.moviechecker.ui.movie.MovieCardModel
import ru.moviechecker.ui.movie.MovieDetailsCardModel

class NewReleasesViewModel(
    private val moviesRepository: MoviesRepository,
    private val episodesRepository: EpisodesRepository
) : ViewModel() {

    val movies = moviesRepository.getNewReleasesStream()
        .map { it.map(MovieCardModel::fromEntity) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptyList()
        )

    private val _movieDetails = MutableStateFlow<MovieDetailsCardModel?>(null)
    val movieDetails: StateFlow<MovieDetailsCardModel?> = _movieDetails

    fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _movieDetails.value =
                MovieDetailsCardModel.fromEntity(moviesRepository.getMovieDetails(movieId))
        }
    }

    fun toggleFavoritesMark(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.getById(movieId).let { movie ->
                movie.favoritesMark = !movie.favoritesMark
                moviesRepository.updateMovie(movie)
            }
        }
    }

    fun markEpisodeViewed(episodeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            episodesRepository.getById(episodeId).let { episode ->
                episode.state = EpisodeState.VIEWED
                episodesRepository.updateEpisode(episode)
            }
        }
    }

    companion object {
        fun provideFactory(
            moviesRepository: MoviesRepository,
            episodesRepository: EpisodesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NewReleasesViewModel(
                    moviesRepository = moviesRepository,
                    episodesRepository = episodesRepository
                ) as T
            }
        }
    }
}
