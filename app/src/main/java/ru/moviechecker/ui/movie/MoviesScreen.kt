package ru.moviechecker.ui.movie

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.moviechecker.R
import ru.moviechecker.ui.theme.MoviecheckerTheme

@Composable
fun MoviesScreen(
    moviesProvider: () -> List<NewMovieCardModel>,
    onClickOnItem: (Int) -> Unit = {},
    onClickOnItemFavorite: (Int) -> Unit = {},
    onClickOnItemViewed: (Int) -> Unit = {},
    onClickOnItemOpenInBrowser: (Int) -> Unit = {},
    showSnackbar: (String) -> Unit = {},
    screenViewModel: MoviesScreenViewModel = viewModel()
) {
    val uiState by screenViewModel.uiState.collectAsStateWithLifecycle()
    val errors by screenViewModel.errors.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val refreshState = rememberPullToRefreshState()

    errors.forEach { error -> showSnackbar(error) }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { screenViewModel.onRefresh(context) },
        state = refreshState
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = moviesProvider(),
                key = { it.id }) { card ->
                AnimatedVisibility(
                    visible = (!uiState.shouldShowOnlyFavorites || card.favoritesMark)
                            && (uiState.shouldShowViewedEpisodes || !card.episode.viewedMark),
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

@Composable
fun MoviesActions(
    showSnackbar: (String) -> Unit = {},
    viewModel: MoviesScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    IconButton(onClick = { viewModel.toggleShouldShowOnlyFavoritesFlag() }) {
        Icon(
            imageVector = if (uiState.shouldShowOnlyFavorites) ImageVector.vectorResource(
                R.drawable.favorite_24px_filled
            ) else ImageVector.vectorResource(R.drawable.favorite_24px),
            tint = if (uiState.shouldShowOnlyFavorites) Color.Yellow else Color.Gray,
            contentDescription = stringResource(R.string.cd_favorites_filter)
        )
    }
    IconButton(onClick = { viewModel.toggleShouldShowViewedEpisodesFlag() }) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.check_24px),
            tint = if (uiState.shouldShowViewedEpisodes) Color.Green else Color.Gray,
            contentDescription = stringResource(R.string.cd_viewed_filter)
        )
    }

    val notImplementedMessage = stringResource(R.string.not_implemented)
    IconButton(onClick = { showSnackbar(notImplementedMessage) }) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.search_24px),
            contentDescription = stringResource(R.string.cd_search)
        )
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
