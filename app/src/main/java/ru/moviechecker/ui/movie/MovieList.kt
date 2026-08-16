package ru.moviechecker.ui.movie

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import ru.moviechecker.ui.ActionsUiState
import ru.moviechecker.ui.theme.MoviecheckerTheme

@Composable
fun MovieList(
    actionsUiState: ActionsUiState,
    moviesProvider: () -> List<MovieCardModel>,
    onClickOnItem: (Int) -> Unit = {},
    onClickOnItemFavorite: (Int) -> Unit = {},
    onClickOnItemViewed: (Int, Boolean) -> Unit = { id, isViewed -> },
    onClickOnItemOpenInBrowser: (Int) -> Unit = {}
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
                    onClickOnViewed = onClickOnItemViewed
                )
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
