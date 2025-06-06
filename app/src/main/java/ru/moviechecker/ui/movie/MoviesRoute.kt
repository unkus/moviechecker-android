package ru.moviechecker.ui.movie

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MoviesRoute(
    viewModel: MovieCardsViewModel,
    openDrawer: () -> Unit,
    navigateToMovieDetails: (movieId: Int) -> Unit,
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val errors by viewModel.errors.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (errors.isNotEmpty()) {
        errors.forEach { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    MoviesScreen(
        uiState = uiState,
        moviesProvider = { movies },
        openDrawer = openDrawer,
        onRefresh = { viewModel.onRefresh(context) },
        onShouldShowOnlyFavoritesIconClick = { viewModel.toggleShouldShowOnlyFavoritesFlag() },
        onShouldShowViewedEpisodesIconClick = { viewModel.toggleShouldShowViewedEpisodesFlag() },
        onMovieClick = { id -> viewModel.markEpisodeViewed(id) },
        onMovieLongClick = { id -> navigateToMovieDetails(id) },
        onFavoriteIconClick = { id -> viewModel.toggleFavoritesMark(id) },
        onViewedIconClick = { id -> viewModel.toggleEpisodeViewedMark(id) },
        navigateBack = navigateBack
    )
}