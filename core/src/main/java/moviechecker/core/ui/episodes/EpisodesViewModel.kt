package moviechecker.core.ui.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import moviechecker.core.CheckerApplication
import moviechecker.core.di.database.episode.Episode
import moviechecker.core.di.database.episode.EpisodeRepository
import javax.inject.Inject

@HiltViewModel
class EpisodesViewModel @Inject constructor(private val repository: EpisodeRepository) : ViewModel() {

    //    val episodes: LiveData<List<IEpisode>> = dao.episodes.asLiveData()
    //    val episodesDetailed: LiveData<List<IEpisode>> = dao.episodesDetailed.asLiveData()

    val released: LiveData<List<Episode>> = repository.released.asLiveData()

    val expected: LiveData<List<Episode>> = repository.expected.asLiveData()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val dataService =
                    (this[APPLICATION_KEY] as CheckerApplication).dataService
                EpisodesViewModel(dataService.episodeRepository)
            }
        }
    }
}