/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.moviechecker.ui.episode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve, update and delete an episode from the [EpisodesRepository]'s data source.
 */
class EpisodeDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val episodesRepository: EpisodesRepository,
) : ViewModel() {

    private val episodeId: Int = checkNotNull(savedStateHandle[EpisodeDetailsDestination.episodeIdArg])

    /**
     * Holds the episode details ui state. The data is retrieved from [EpisodesRepository] and mapped to
     * the UI state.
     */
    val uiState: StateFlow<EpisodeDetailsUiState> =
        episodesRepository.getByIdStream(episodeId)
            .filterNotNull()
            .map {
                EpisodeDetailsUiState(state = it.state, isFavorite = false, episodeDetails = it.toEpisodeDetails())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = EpisodeDetailsUiState()
            )


    /**
     * Mark the episode viewed.
     */
    fun setViewed() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentEpisode = uiState.value.episodeDetails.toEntity()
            currentEpisode.state = EpisodeState.VIEWED
            episodesRepository.updateEpisode(currentEpisode)
        }
    }

    /**
     * Add the episode to favorites
     */
    fun addToFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentEpisode = uiState.value.episodeDetails.toEntity()
            episodesRepository.updateEpisode(currentEpisode)
        }
    }

    /**
     * Remove the episode from favorites
     */
    fun removeFromFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentEpisode = uiState.value.episodeDetails.toEntity()
            episodesRepository.updateEpisode(currentEpisode)
        }
    }

    /**
     * Deletes the episode from the [EpisodesRepository]'s data source.
     */
    suspend fun deleteEpisode() {
        viewModelScope.launch(Dispatchers.IO) {
            episodesRepository.deleteEpisode(uiState.value.episodeDetails.toEntity())
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for ItemDetailsScreen
 */
data class EpisodeDetailsUiState(
    val state: EpisodeState = EpisodeState.UNKNOWN,
    val isFavorite: Boolean = true,
    val episodeDetails: EpisodeDetails = EpisodeDetails()
)
