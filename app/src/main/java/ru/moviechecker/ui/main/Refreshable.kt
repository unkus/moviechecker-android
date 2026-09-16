package ru.moviechecker.ui.main

import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable

@Composable
fun Refreshable(
    refreshStatus: Boolean,
    onRefresh: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val refreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = refreshStatus,
        onRefresh = onRefresh,
        state = refreshState
    ) {
        content()
    }
}
