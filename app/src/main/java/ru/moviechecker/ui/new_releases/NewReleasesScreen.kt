package ru.moviechecker.ui.new_releases

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.moviechecker.ui.ActionsViewModel
import ru.moviechecker.ui.catalog.MovieList
import ru.moviechecker.ui.movie.MovieCardModel

@Composable
fun NewReleasesScreen(
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