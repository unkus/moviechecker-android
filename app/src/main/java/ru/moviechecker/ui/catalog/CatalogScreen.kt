package ru.moviechecker.ui.catalog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import ru.moviechecker.ui.common.CommonList
import ru.moviechecker.ui.main.Refreshable
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
    onClickOnItemOpenInBrowser: (Int) -> Unit = {}
) {
    Refreshable(
        onRefresh = onRefresh
    ) {
        CommonList(
            items = moviesProvider(),
            itemContent = { item ->
                MovieCard(
                    cardProvider = { item },
                    onClick = onClickOnItem,
                    onClickOnFavorite = onClickOnItemFavorite,
                    onClickOnViewed = onClickOnItemViewed,
                    onActionPerformed = onClickOnItemOpenInBrowser
                )
            }
        )
    }
}

@Preview(name = "Светлая тема", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Темная тема", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CatalogScreenPreview() {
    MoviecheckerTheme {
        CatalogScreen(
            moviesProvider = { MovieCardPreviewParameterProvider().values.toList() }
        )
    }
}