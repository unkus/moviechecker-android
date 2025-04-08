package ru.moviechecker.ui.site

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SitesRoute(
    viewModel: SitesViewModel,
    openDrawer: () -> Unit,
    navigateToMovies: (Int) -> Unit
) {
    val sites by viewModel.sites.collectAsStateWithLifecycle()

    SitesScreen(
        sitesProvider = { sites },
        openDrawer = openDrawer,
        onSiteClick = { id -> navigateToMovies(id) }
    )
}