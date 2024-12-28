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

package ru.moviechecker.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import ru.moviechecker.CheckerApplication
import ru.moviechecker.ui.episode.EpisodeDetailsViewModel
import ru.moviechecker.ui.episode.EpisodeEditViewModel
import ru.moviechecker.ui.episode.EpisodeEntryViewModel
import ru.moviechecker.ui.home.HomeViewModel
import ru.moviechecker.ui.movie.MovieCardsViewModel
import ru.moviechecker.ui.movie.MovieDetailsViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Checker app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for EpisodeEditViewModel
        initializer {
            EpisodeEditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                episodesRepository = checkerApplication().container.episodesRepository
            )
        }
        // Initializer for EpisodeEntryViewModel
        initializer {
            EpisodeEntryViewModel(
                episodesRepository = checkerApplication().container.episodesRepository
            )
        }

        // Initializer for EpisodeDetailsViewModel
        initializer {
            EpisodeDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                episodesRepository = checkerApplication().container.episodesRepository
            )
        }

        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                episodesRepository = checkerApplication().container.episodesRepository,
                moviesRepository = checkerApplication().container.moviesRepository
            )
        }

        initializer {
            MovieCardsViewModel(
                moviesRepository = checkerApplication().container.moviesRepository,
                episodesRepository = checkerApplication().container.episodesRepository
            )
        }

        initializer {
            MovieDetailsViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                moviesRepository = checkerApplication().container.moviesRepository,
                seasonsRepository = checkerApplication().container.seasonsRepository,
                episodesRepository = checkerApplication().container.episodesRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [CheckerApplication].
 */
fun CreationExtras.checkerApplication(): CheckerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CheckerApplication)
