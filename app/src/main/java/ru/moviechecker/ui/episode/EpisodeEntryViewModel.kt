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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.moviechecker.database.episodes.EpisodeEntity
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * ViewModel to validate and insert episodes in the Room database.
 */
class EpisodeEntryViewModel(private val episodesRepository: EpisodesRepository) : ViewModel() {

    /**
     * Holds current episode ui state
     */
    var episodeUiState by mutableStateOf(EpisodeUiState())
        private set

    /**
     * Updates the [episodeUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(episodeDetails: EpisodeDetails) {
        episodeUiState =
            EpisodeUiState(episodeDetails = episodeDetails, isEntryValid = validateInput(episodeDetails))
    }

    /**
     * Inserts an [EpisodeEntity] in the Room database
     */
    suspend fun saveEpisode() {
        if (validateInput()) {
            viewModelScope.launch(Dispatchers.IO) {
                episodesRepository.insertEpisode(episodeUiState.episodeDetails.toEntity())
            }
        }
    }

    private fun validateInput(uiState: EpisodeDetails = episodeUiState.episodeDetails): Boolean {
        return with(uiState) {
            link.isNotBlank()
        }
    }
}

/**
 * Represents Ui State for an Episode.
 */
data class EpisodeUiState(
    val episodeDetails: EpisodeDetails = EpisodeDetails(),
    val isEntryValid: Boolean = false
)

data class EpisodeDetails(
    val number: Int = 0,
    val title: String? = null,
    val link: String = "",
    val state: String = "",
    val date: String = ""
)

/**
 * Extension function to convert [EpisodeUiState] to [EpisodeEntity]. If the value of [EpisodeDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [EpisodeUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun EpisodeDetails.toEntity(): EpisodeEntity {
    return EpisodeEntity(
        number = 1,
        title = title,
        link = link,
        state = EpisodeState.valueOf(state),
        date = LocalDateTime.parse(date, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
    )
}

/**
 * Extension function to convert [EpisodeEntity] to [EpisodeUiState]
 */
fun EpisodeEntity.toEpisodeUiState(isEntryValid: Boolean = false): EpisodeUiState = EpisodeUiState(
    episodeDetails = this.toEpisodeDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [EpisodeEntity] to [EpisodeDetails]
 */
fun EpisodeEntity.toEpisodeDetails(): EpisodeDetails = EpisodeDetails(
    number = number,
    title = title,
    link = link.toString(),
    state = state.name,
    date = date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
)
