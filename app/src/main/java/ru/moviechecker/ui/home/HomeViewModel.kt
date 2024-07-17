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

package ru.moviechecker.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.moviechecker.database.episodes.EpisodeState
import ru.moviechecker.database.episodes.EpisodesRepository
import ru.moviechecker.database.episodes.IEpisodeView
import ru.moviechecker.database.movies.MoviesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to retrieve all items in the Room database.
 */
class HomeViewModel(
    private val episodesRepository: EpisodesRepository,
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [EpisodesRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
        episodesRepository.getReleasedEpisodesViewStream().map {
            Log.d(this.javaClass.simpleName, "фильмов: ${moviesRepository.getAll().size}")
            Log.d(this.javaClass.simpleName, "серий: ${it.size}")
            HomeUiState(it)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    /**
     * Mark the episode viewed.
     */
    fun markEpisodeViewed(id: Int) {
        viewModelScope.launch {
            episodesRepository.getById(id)?.let {
                it.state = EpisodeState.VIEWED
                episodesRepository.updateEpisode(it)
            }
        }
    }

    /**
     * Mark/unmark the movie as favorite.
     */
    fun switchFavoritesMark(id: Int) {
        viewModelScope.launch {
            moviesRepository.getById(id)?.let {
                it.favoritesMark = !it.favoritesMark
                moviesRepository.updateMovie(it)
            }
        }
    }

    /**
     * Mark/unmark the episode as viewed.
     */
    fun switchViewedMark(id: Int) {
        viewModelScope.launch {
            episodesRepository.getById(id)?.let {
                if (EpisodeState.VIEWED == it.state) {
                    it.state = EpisodeState.RELEASED;
                } else if (EpisodeState.RELEASED == it.state) {
                    it.state = EpisodeState.VIEWED;
                }
                episodesRepository.updateEpisode(it)
            }
        }
    }

}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val episodeList: List<IEpisodeView> = listOf())
