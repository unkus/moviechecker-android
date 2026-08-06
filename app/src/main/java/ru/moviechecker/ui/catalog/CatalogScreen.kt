package ru.moviechecker.ui.catalog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.ui.ActionsUiState
import ru.moviechecker.ui.ActionsViewModel
import ru.moviechecker.ui.main.ErrorViewModel
import ru.moviechecker.ui.main.RefreshViewModel
import ru.moviechecker.ui.movie.MovieCard
import ru.moviechecker.ui.movie.MovieCardModel
import ru.moviechecker.ui.movie.MovieCardPreviewParameterProvider
import ru.moviechecker.ui.theme.MoviecheckerTheme

@Composable
fun CatalogScreen(
    moviesProvider: () -> List<MovieCardModel>,
    onClickOnItem: (Int) -> Unit = {},
    onClickOnItemFavorite: (Int) -> Unit = {},
    onClickOnItemViewed: (Int, Boolean) -> Unit = { id, isViewed -> },
    onClickOnItemOpenInBrowser: (Int) -> Unit = {},
    actionViewModel: ActionsViewModel = viewModel()
) {
    val actionsUiState by actionViewModel.uiState.collectAsStateWithLifecycle()

    MovieList(
        actionsUiState = actionsUiState,
        moviesProvider = moviesProvider,
        onClickOnItem = onClickOnItem,
        onClickOnItemFavorite = onClickOnItemFavorite,
        onClickOnItemViewed = onClickOnItemViewed,
        onClickOnItemOpenInBrowser = onClickOnItemOpenInBrowser
    )
}

@Composable
fun MovieList(
    actionsUiState: ActionsUiState,
    moviesProvider: () -> List<MovieCardModel>,
    onClickOnItem: (Int) -> Unit = {},
    onClickOnItemFavorite: (Int) -> Unit = {},
    onClickOnItemViewed: (Int, Boolean) -> Unit = { id, isViewed -> },
    onClickOnItemOpenInBrowser: (Int) -> Unit = {},
    refreshViewModel: RefreshViewModel = viewModel(),
    errorViewModel: ErrorViewModel = viewModel()
) {
    val refreshUiState by refreshViewModel.uiState.collectAsStateWithLifecycle()

    val refreshState = rememberPullToRefreshState()

    refreshUiState.error?.let { error -> errorViewModel.triggerError(error) }

    PullToRefreshBox(
        isRefreshing = refreshUiState.isLoading,
        onRefresh = { refreshViewModel.onRefresh() },
        state = refreshState
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = moviesProvider(),
                key = { it.id }) { card ->
                AnimatedVisibility(
                    visible = (!actionsUiState.shouldShowNonFavorites || card.favoritesMark)
                            && (actionsUiState.shouldShowViewedEpisodes || !card.episode.viewedMark),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    MovieCard(
                        cardProvider = { card },
                        onClick = onClickOnItem,
                        onClickOnFavorite = onClickOnItemFavorite,
                        onClickOnViewed = onClickOnItemViewed,
                        onClickOnOpenInBrowser = onClickOnItemOpenInBrowser
                    )
                }
            }
        }
    }
}

@Preview(name = "Светлая тема", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Темная тема", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CatalogScreenPreview() {
    MoviecheckerTheme {
        MovieList(
            actionsUiState = ActionsUiState(),
            moviesProvider = { MovieCardPreviewParameterProvider().values.toList() }
        )
    }
}
