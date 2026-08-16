package ru.moviechecker.ui.main

import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Refreshable(
    refreshViewModel: RefreshViewModel = viewModel(),
    errorViewModel: ErrorViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val refreshUiState by refreshViewModel.uiState.collectAsStateWithLifecycle()

    val refreshState = rememberPullToRefreshState()

    refreshUiState.error?.let { error -> errorViewModel.triggerError(error) }

    PullToRefreshBox(
        isRefreshing = refreshUiState.isLoading,
        onRefresh = { refreshViewModel.onRefresh() },
        state = refreshState
    ) {
        content()
    }
}
