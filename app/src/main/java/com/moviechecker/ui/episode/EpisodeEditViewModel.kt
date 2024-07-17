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

package com.moviechecker.ui.episode

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviechecker.database.episodes.EpisodesRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve and update an item from the [EpisodesRepository]'s data source.
 */
class EpisodeEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val episodesRepository: EpisodesRepository
) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var episodeUiState by mutableStateOf(EpisodeUiState())
        private set

    private val episodeId: Int = checkNotNull(savedStateHandle[EpisodeEditDestination.episodeIdArg])

    init {
        viewModelScope.launch {
            episodeUiState = episodesRepository.getByIdStream(episodeId)
                .filterNotNull()
                .first()
                .toEpisodeUiState(true)
        }
    }

    /**
     * Update the episode in the [EpisodesRepository]'s data source
     */
    suspend fun updateEpisode() {
        if (validateInput(episodeUiState.episodeDetails)) {
            episodesRepository.updateEpisode(episodeUiState.episodeDetails.toEntity())
        }
    }

    /**
     * Updates the [episodeUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(episodeDetails: EpisodeDetails) {
        episodeUiState =
            EpisodeUiState(episodeDetails = episodeDetails, isEntryValid = validateInput(episodeDetails))
    }

    private fun validateInput(uiState: EpisodeDetails = episodeUiState.episodeDetails): Boolean {
        return with(uiState) {
            link.isNotBlank()
        }
    }
}
