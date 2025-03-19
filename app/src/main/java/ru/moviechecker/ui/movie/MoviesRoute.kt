package ru.moviechecker.ui.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MoviesRoute(
    viewModel: MovieCardsViewModel,
    openDrawer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val context = LocalContext.current

    MoviesScreen(
        uiState = uiState,
        moviesProvider = { movies },
        openDrawer = openDrawer,
        onRefresh = { viewModel.onRefresh(context) },
        onShouldShowOnlyFavoritesIconClick = { viewModel.toggleShouldShowOnlyFavoritesFlag() },
        onShouldShowViewedEpisodesIconClick = { viewModel.toggleShouldShowViewedEpisodesFlag() },
        onMovieClick = { id -> viewModel.markEpisodeViewed(id) },
        onMovieLongClick = { },
        onFavoriteIconClick = { id -> viewModel.toggleFavoritesMark(id) },
        onViewedIconClick = { id -> viewModel.toggleEpisodeViewedMark(id) },
    )
}