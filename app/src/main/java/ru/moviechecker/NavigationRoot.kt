package ru.moviechecker

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ru.moviechecker.database.AppContainer
import ru.moviechecker.database.DefaultAppContainer
import ru.moviechecker.ui.movie.MoviesActions
import ru.moviechecker.ui.movie.MoviesDestination
import ru.moviechecker.ui.movie.MoviesViewModel
import ru.moviechecker.ui.site.SitesDestination
import ru.moviechecker.ui.site.SitesViewModel
import ru.moviechecker.ui.theme.MoviecheckerTheme

enum class AppDestinations(
    val label: Int,
    val icon: Int
) {
    MOVIES(
        R.string.movies_episodes,
        R.drawable.menu_24px
    ),
    SITES(R.string.sites, R.drawable.menu_24px),
}

/**
 * Top level composable that represents screens for the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRoot(
    appContainer: AppContainer,
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MOVIES) }
    val scope = rememberCoroutineScope()

    val navigationSuiteScaffoldState =
        rememberNavigationSuiteScaffoldState(initialValue = NavigationSuiteScaffoldValue.Hidden)

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    selected = currentDestination == destination,
                    onClick = {
                        currentDestination = destination
                        scope.launch {
                            navigationSuiteScaffoldState.hide()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(destination.icon),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(id = destination.label)) }
                )
            }
        }
    ) {
        MainScreen(currentDestination, appContainer)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    destination: AppDestinations,
    appContainer: AppContainer
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(destination.label)) },
                actions = {
                    when (destination) {
                        AppDestinations.MOVIES -> MoviesActions(
                            showSnackbar = { msg ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        msg
                                    )
                                }
                            }
                        )

                        else -> {}
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        when (destination) {
            AppDestinations.MOVIES -> MoviesDestination(
                innerPadding = innerPadding,
                viewModel = viewModel(
                    factory = MoviesViewModel.provideFactory(
                        moviesRepository = appContainer.moviesRepository,
                        episodesRepository = appContainer.episodesRepository
                    )
                ),
                showSnackbar = { msg ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            msg
                        )
                    }
                }
            )

            AppDestinations.SITES -> SitesDestination(
                innerPadding = innerPadding,
                viewModel = viewModel(factory = SitesViewModel.provideFactory(appContainer.sitesRepository))
            )
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen(
    @PreviewParameter(MainScreenPreviewParameterProvider::class) destination: Int
) {
    val context = LocalContext.current

    MoviecheckerTheme {
        MainScreen(
            destination = AppDestinations.entries[destination],
            appContainer = DefaultAppContainer(
                context = context
            )
        )
    }
}

class MainScreenPreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(
        AppDestinations.MOVIES.ordinal,
        AppDestinations.SITES.ordinal
    )
}