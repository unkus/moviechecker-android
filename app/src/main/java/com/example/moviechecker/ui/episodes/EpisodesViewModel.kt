package com.example.moviechecker.ui.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.moviechecker.CheckerApplication
import com.example.moviechecker.model.episode.Episode
import com.example.moviechecker.model.episode.EpisodeDao
import com.example.moviechecker.model.episode.EpisodeDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EpisodesViewModel(private val dao: EpisodeDao): ViewModel() {

    val episodes: LiveData<List<Episode>> = dao.episodes.asLiveData()
    val episodesDetailed: LiveData<List<EpisodeDetail>> = dao.episodesDetailed.asLiveData()

    suspend fun loadBySeasonAndNumber(season_id: Int, number: Int): Episode? {
        return dao.loadBySeasonAndNumber(season_id, number)
    }

    fun loadReleased(): LiveData<List<EpisodeDetail>> = dao.loadReleased().asLiveData()

    fun loadExpected(): LiveData<List<EpisodeDetail>> = dao.loadExpected().asLiveData()

    fun insert(episode: Episode) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(episode)
    }

    fun update(episode: Episode) = viewModelScope.launch(Dispatchers.IO) {
        dao.update(episode)
    }

    fun delete(episode: Episode) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(episode)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        dao.deleteAll()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val database = (this[APPLICATION_KEY] as CheckerApplication).database
                EpisodesViewModel(database.episodeDao())
            }
        }
    }
}