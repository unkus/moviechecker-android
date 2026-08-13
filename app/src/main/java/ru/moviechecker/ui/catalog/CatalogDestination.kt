package ru.moviechecker.ui.catalog

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
fun CatalogDestination(
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

    val movies by viewModel.movies.collectAsStateWithLifecycle()

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                CatalogScreen(
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
                    onClickOnItemViewed = viewModel::toggleEpisodeViewedMark,
                    onClickOnItemOpenInBrowser = viewModel::markEpisodeViewed
                )
            }
        },
        detailPane = {
            AnimatedPane {
                navigator.currentDestination?.contentKey?.let { movieId ->
                    viewModel.loadDetails(movieId)
                    val details by viewModel.movieDetails.collectAsStateWithLifecycle()

                    details?.let { details ->
                        MovieDetailsScreen(
                            details = details,
                            saveAction = { kinopoiskId ->
                                viewModel.update(details.id, kinopoiskId)
                            },
                            onClickOnFavorite = { viewModel.toggleFavoritesMark(movieId) },
                            onClickOnEpisodeViewed = viewModel::toggleEpisodeViewedMark,
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