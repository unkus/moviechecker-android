package ru.moviechecker.ui.movie

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.moviechecker.R
import ru.moviechecker.ui.theme.MoviecheckerTheme
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    details: MovieDetailsCardModel,
    saveAction: (String) -> Unit = {},
    onClickOnFavorite: () -> Unit = {},
    onClickOnEpisodeViewed: (Int, Boolean) -> Unit = { id, isViewed -> },
    onClickOnBackArrow: () -> Unit = {},
    screenViewModel: MovieDetailsScreenViewModel = viewModel()
) {
    val uiState by screenViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(details.title)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onClickOnBackArrow
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow_back_24px),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(contentPadding = innerPadding) {
            item {
                MovieDetailsCard(
                    movie = details,
                    saveAction = saveAction,
                    onFavoriteIconClick = { onClickOnFavorite() }
                )
            }
            items(items = details.seasons, key = { listOf(it.number) }) { season ->
                MovieDetailsSeasonCard(
                    season = season,
                    isExpanded = uiState.expandedSeasonNumber == season.number,
                    onClickOnExpand = { screenViewModel.expandSeason(season.number) },
                    onClickOnEpisodeViewed = onClickOnEpisodeViewed,
                )
            }
        }
    }
}

@Preview("Детализации")
@Preview("Детализации (темная тема)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewMovieDetailsScreen() {
    MoviecheckerTheme {
        MovieDetailsScreen(
            details = MovieDetailsCardModel(
                id = 1,
                siteId = 1,
                pageId = "movie_page_id",
                title = "Фильм такой-то",
                poster = poster,
                favoritesMark = true,
                seasons = listOf(
                    SeasonCardModel(
                        id = 1,
                        number = 1,
                        poster = poster,
                        episodes = listOf(
                            EpisodeCardModel(
                                id = 1,
                                number = 1,
                                link = "stub",
                                date = LocalDateTime.now(),
                                viewedMark = true
                            ),
                            EpisodeCardModel(
                                id = 2,
                                number = 2,
                                link = "stub",
                                date = LocalDateTime.now(),
                                viewedMark = false
                            )
                        )
                    ),
                    SeasonCardModel(
                        id = 2,
                        number = 2,
                        poster = poster,
                        episodes = listOf(
                            EpisodeCardModel(
                                id = 1,
                                number = 1,
                                link = "stub",
                                date = LocalDateTime.now(),
                                viewedMark = true
                            ),
                            EpisodeCardModel(
                                id = 2,
                                number = 2,
                                link = "stub",
                                date = LocalDateTime.now(),
                                viewedMark = true
                            )
                        )
                    ),
                    SeasonCardModel(
                        id = 3,
                        number = 3,
                        poster = poster,
                        episodes = listOf()
                    )
                )
            )
        )
    }
}
