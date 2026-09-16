package ru.moviechecker.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlinx.coroutines.launch
import ru.moviechecker.R
import ru.moviechecker.ui.SearchAction
import ru.moviechecker.ui.ShowNonFavoritesAction
import ru.moviechecker.ui.ShowViewedAction
import ru.moviechecker.ui.catalog.CatalogDestination
import ru.moviechecker.ui.expected.ExpectedDestination
import ru.moviechecker.ui.main.MainScreen
import ru.moviechecker.ui.novelties.NoveltiesDestination
import ru.moviechecker.ui.site.SitesDestination
import ru.moviechecker.ui.theme.MoviecheckerTheme

enum class AppDestinations(
    val label: Int,
    val icon: Int
) {
    NOVELTIES(
        R.string.novelties,
        R.drawable.menu_24px
    ),
    EXPECTED(
        R.string.expected,
        R.drawable.menu_24px
    ),
    CATALOG(
        R.string.catalog,
        R.drawable.menu_24px
    ),
    SITES(R.string.sites, R.drawable.menu_24px),
}

/**
 * Top level composable that represents screens for the application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRoot() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.NOVELTIES) }
    val scope = rememberCoroutineScope()

    val navigationSuiteScaffoldState =
        rememberNavigationSuiteScaffoldState()

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
        MainScreen(
            label = currentDestination.label,
            actions = {
                when (currentDestination) {

                    AppDestinations.NOVELTIES -> {
                        ShowNonFavoritesAction()
                    }

                    AppDestinations.EXPECTED -> {
                        ShowNonFavoritesAction()
                    }

                    AppDestinations.CATALOG -> {
                        ShowNonFavoritesAction()
                        ShowViewedAction()
                        SearchAction()
                    }

                    else -> {}
                }
            }
        ) { innerPadding ->
            when (currentDestination) {
                AppDestinations.NOVELTIES -> NoveltiesDestination(
                    innerPadding = innerPadding
                )

                AppDestinations.EXPECTED -> ExpectedDestination(
                    innerPadding = innerPadding
                )

                AppDestinations.CATALOG -> CatalogDestination(
                    innerPadding = innerPadding
                )

                AppDestinations.SITES -> SitesDestination(
                    innerPadding = innerPadding
                )
            }
        }
    }
}

@Preview
@Composable
fun NavigationRootPreview(
    @PreviewParameter(NavigationRootPreviewParameterProvider::class) destination: Int
) {
    MoviecheckerTheme {
        NavigationRoot()
    }
}

class NavigationRootPreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values = AppDestinations.entries
        .map(AppDestinations::ordinal)
        .asSequence()
}
