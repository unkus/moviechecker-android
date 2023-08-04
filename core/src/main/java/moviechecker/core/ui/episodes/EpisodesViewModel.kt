package moviechecker.core.ui.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import moviechecker.core.di.database.episode.Episode
import moviechecker.core.di.database.episode.EpisodeRepository
import javax.inject.Inject

@HiltViewModel
class EpisodesViewModel @Inject constructor(repository: EpisodeRepository) : ViewModel() {

    val released: LiveData<List<Episode>> = repository.released.asLiveData()

    val expected: LiveData<List<Episode>> = repository.expected.asLiveData()

}