package ru.moviechecker.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import ru.moviechecker.R
import ru.moviechecker.ui.main.ErrorViewModel
import ru.moviechecker.ui.theme.MoviecheckerTheme

@Composable
fun ShowNonFavoritesAction(
    viewModel: ActionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    IconButton(onClick = { viewModel.toggleShouldShowNonFavoritesFlag() }) {
        Icon(
            imageVector = if (uiState.shouldShowNonFavorites) ImageVector.vectorResource(
                R.drawable.favorite_24px_filled
            ) else ImageVector.vectorResource(R.drawable.favorite_24px),
            tint = if (uiState.shouldShowNonFavorites) Color.Yellow else Color.Gray,
            contentDescription = stringResource(R.string.cd_favorites_filter)
        )
    }
}

@Preview(name = "Светлая тема", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Темная тема", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ShowNonFavoritesActionPreview() {
    MoviecheckerTheme {
        ShowNonFavoritesAction()
    }
}

@Composable
fun ShowViewedAction(
    viewModel: ActionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    IconButton(onClick = { viewModel.toggleShouldShowViewedEpisodesFlag() }) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.check_24px),
            tint = if (uiState.shouldShowViewedEpisodes) Color.Green else Color.Gray,
            contentDescription = stringResource(R.string.cd_viewed_filter)
        )
    }
}

@Preview(name = "Светлая тема", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Темная тема", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ShowViewedActionPreview() {
    MoviecheckerTheme {
        ShowViewedAction()
    }
}

@Composable
fun SearchAction(
    errorViewModel: ErrorViewModel = viewModel()
) {
    val notImplementedMessage = stringResource(R.string.not_implemented)

    IconButton(onClick = { errorViewModel.triggerError(notImplementedMessage) }) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.search_24px),
            contentDescription = stringResource(R.string.cd_search)
        )
    }
}

@Preview(name = "Светлая тема", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Темная тема", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SearchActionPreview() {
    MoviecheckerTheme {
        SearchAction()
    }
}

class ActionsViewModel : ViewModel() {
    private val _viewModelState = MutableStateFlow(
        ActionsUiState(
            shouldShowNonFavorites = false,
            shouldShowViewedEpisodes = true
        )
    )

    val uiState = _viewModelState
        .map { state ->
            ActionsUiState(
                shouldShowNonFavorites = state.shouldShowNonFavorites,
                shouldShowViewedEpisodes = state.shouldShowViewedEpisodes
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = _viewModelState.value
        )

    fun toggleShouldShowNonFavoritesFlag() {
        _viewModelState.update {
            it.copy(
                shouldShowNonFavorites = !it.shouldShowNonFavorites
            )
        }
    }

    fun toggleShouldShowViewedEpisodesFlag() {
        _viewModelState.update {
            it.copy(
                shouldShowViewedEpisodes = !it.shouldShowViewedEpisodes
            )
        }
    }
}

data class ActionsUiState(
    val shouldShowNonFavorites: Boolean = false,
    val shouldShowViewedEpisodes: Boolean = true
)