package ru.moviechecker.ui.novelties

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import ru.moviechecker.ui.common.CommonList
import ru.moviechecker.ui.main.Refreshable
import ru.moviechecker.ui.movie.MovieCardContent
import ru.moviechecker.ui.movie.MovieCardModel
import ru.moviechecker.ui.movie.MovieCardPreviewParameterProvider
import ru.moviechecker.ui.theme.MoviecheckerTheme

@Composable
fun NoveltiesScreen(
    moviesProvider: () -> List<MovieCardModel>,
    refreshStatus: () -> Boolean = { false },
    onRefresh: () -> Unit = {},
    onClickOnItem: (Int) -> Unit = {},
    onClickOnItemFavorite: (Int) -> Unit = {},
    onClickOnItemViewed: (Int, Boolean) -> Unit = { id, isViewed -> },
    onClickOnItemOpenInBrowser: (Int) -> Unit = {}
) {
    Refreshable(
        refreshStatus = refreshStatus(),
        onRefresh = onRefresh
    ) {
        CommonList(
            items = moviesProvider(),
            itemContent = { movie ->
                MovieCardContent(
                    dataProvider = { movie },
                    onClickOnFavorite = onClickOnItemFavorite,
                    onClickOnViewed = onClickOnItemViewed,
                    onActionPerformed = onClickOnItemOpenInBrowser
                )
            },
            modifier = Modifier.fillMaxSize(),
            onItemClick = { movie -> onClickOnItem(movie.id) }
        )
    }
}

@Preview(name = "Светлая тема", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Темная тема", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun CatalogScreenPreview() {
    MoviecheckerTheme {
        NoveltiesScreen(
            moviesProvider = { MovieCardPreviewParameterProvider().values.toList() }
        )
    }
}