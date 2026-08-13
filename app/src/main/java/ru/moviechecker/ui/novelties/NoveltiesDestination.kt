package ru.moviechecker.ui.novelties

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.moviechecker.ui.movie.MovieDetailsScreen
import ru.moviechecker.ui.movie.MoviesViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NoveltiesDestination(
    innerPadding: PaddingValues,
    viewModel: MoviesViewModel
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val scope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        scope.launch {
            navigator.navigateBack()
        }
    }

    val movies by viewModel.novelties.collectAsStateWithLifecycle()

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                NoveltiesScreen(
                    moviesProvider = { movies },
                    onClickOnItem = { movieId ->
                        scope.launch {
                            navigator.navigateTo(
                                ListDetailPaneScaffoldRole.Detail,
                                movieId
                            )
                        }
                    },
                    onClickOnItemFavorite = viewModel::toggleFavoritesMark,
                    onClickOnItemViewed = { episodeId, viewedMark ->
                        viewModel.markEpisodeViewed(
                            episodeId
                        )
                    },
                    onClickOnItemOpenInBrowser = { }
                )
            }
        },
        detailPane = {
            AnimatedPane {
                navigator.currentDestination?.contentKey?.let { movieId ->
                    viewModel.loadDetails(movieId)
                    val details by viewModel.movieDetails.collectAsStateWithLifecycle()

                    details?.let { model ->
                        MovieDetailsScreen(
                            details = model,
                            saveAction = { kinopoiskId ->
                                viewModel.update(model.id, kinopoiskId)
                            },
                            onClickOnFavorite = { viewModel.toggleFavoritesMark(movieId) },
                            onClickOnEpisodeViewed = { episodeId, viewedMark ->
                                viewModel.markEpisodeViewed(
                                    episodeId
                                )
                            },
                            onClickOnBackArrow = {
                                scope.launch {
                                    navigator.navigateBack()
                                }
                            }
                        )
                    }
                }
            }
        },
        modifier = Modifier.padding(innerPadding)
    )
}