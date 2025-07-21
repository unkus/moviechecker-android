package ru.moviechecker.ui.site

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SiteDetailsRoute(
    viewModel: SiteDetailsViewModel,
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SiteDetailsScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onUseMirrorToggle = viewModel::setUseMirror,
        onMirrorChanged = viewModel::setMirror
    )
}