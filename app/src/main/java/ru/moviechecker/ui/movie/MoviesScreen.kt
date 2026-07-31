package ru.moviechecker.ui.movie

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.moviechecker.ui.ActionsViewModel
import ru.moviechecker.ui.theme.MoviecheckerTheme

@Composable
fun MoviesScreen(
    moviesProvider: () -> List<MovieCardModel>,
    onClickOnItem: (Int) -> Unit = {},
    onClickOnItemFavorite: (Int) -> Unit = {},
    onClickOnItemViewed: (Int) -> Unit = {},
    onClickOnItemOpenInBrowser: (Int) -> Unit = {},
    viewModel: MoviesScreenViewModel = viewModel(),
    actionViewModel: ActionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionsUiState by actionViewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val refreshState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        viewModel.errors.collect { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.onRefresh() },
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

@Preview
@Preview(name = "Темная тема", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MovieScreenPreview() {
    MoviecheckerTheme {
        MoviesScreen(
            moviesProvider = { MovieCardPreviewParameterProvider().values.toList() }
        )
    }
}
