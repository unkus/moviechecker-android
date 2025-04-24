package ru.moviechecker.ui.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MoviesRoute(
    viewModel: MovieCardsViewModel,
    openDrawer: () -> Unit,
    navigateToMovieDetails: (Int) -> Unit,
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val site by viewModel.site.collectAsStateWithLifecycle()
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val context = LocalContext.current

    MoviesScreen(
        uiState = uiState,
        site = site.title,
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