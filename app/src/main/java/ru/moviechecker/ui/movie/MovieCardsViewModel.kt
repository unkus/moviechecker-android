package ru.moviechecker.ui.movie

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.movies.MovieCardsView
import ru.moviechecker.database.movies.MoviesRepository

class MovieCardsViewModel(
    private val moviesRepository: MoviesRepository,
    private val episodesRepository: EpisodesRepository
) : ViewModel() {

    val uiState: StateFlow<MoviesUiState> = moviesRepository.getMovieCardsStream()
        .map { MoviesUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = MoviesUiState()
        )

    fun switchFavoritesMark(movieId: Int) {
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

    fun switchEpisodeViewedMark(episodeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            episodesRepository.findById(episodeId)?.let { episode ->
                episode.state = if (episode.state == EpisodeState.VIEWED) {
                    EpisodeState.RELEASED
                } else if(episode.state == EpisodeState.RELEASED) {
                    EpisodeState.VIEWED
                } else {
                    episode.state
                }
                episodesRepository.updateEpisode(episode)
            }
        }
    }

}

data class MoviesUiState(
    val movies: List<MovieCardsView> = listOf(),
    val shouldShowOnlyFavorites: MutableState<Boolean> = mutableStateOf(false),
    val shouldShowViewedEpisodes: MutableState<Boolean> = mutableStateOf(true),
    val refreshing: Boolean = false
)
