package ru.moviechecker.ui.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MovieDetailsRoute(
    viewModel: MovieDetailsViewModel,
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val movie by viewModel.movie.collectAsStateWithLifecycle()
    val seasons by viewModel.seasons.collectAsStateWithLifecycle()
    val context = LocalContext.current

    MovieDetailsScreen(
        uiState = uiState,
        movieProvider = { movie },
        seasonsProvider = { seasons },
        onRefresh = { viewModel.onRefresh(context) },
        onSeasonExpanded = { seasonNumber -> viewModel.setExpandedSeason(seasonNumber) },
        onFavoriteIconClick = { id -> viewModel.toggleFavoritesMark(id) },
        onEpisodeClick = { episodeId -> viewModel.toggleEpisodeViewedMark(episodeId) },
        onEpisodeViewedMarkIconClick = { episodeId -> viewModel.toggleEpisodeViewedMark(episodeId) },
        navigateBack = navigateBack
    )
}