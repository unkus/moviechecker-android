package ru.moviechecker.ui.expected

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.moviechecker.ui.ActionsViewModel
import ru.moviechecker.ui.main.Refreshable
import ru.moviechecker.ui.movie.MovieCardModel
import ru.moviechecker.ui.movie.MovieList

@Composable
fun ExpectedScreen(
    moviesProvider: () -> List<MovieCardModel>,
    onClickOnItem: (Int) -> Unit = {},
    onClickOnItemFavorite: (Int) -> Unit = {},
    onClickOnItemViewed: (Int, Boolean) -> Unit = { id, isViewed -> },
    onClickOnItemOpenInBrowser: (Int) -> Unit = {},
    actionViewModel: ActionsViewModel = viewModel()
) {
    val actionsUiState by actionViewModel.uiState.collectAsStateWithLifecycle()

    Refreshable {
        MovieList(
            actionsUiState = actionsUiState,
            moviesProvider = moviesProvider,
            onClickOnItem = onClickOnItem,
            onClickOnItemFavorite = onClickOnItemFavorite,
            onClickOnItemViewed = onClickOnItemViewed,
            onClickOnItemOpenInBrowser = onClickOnItemOpenInBrowser
        )
    }
}
