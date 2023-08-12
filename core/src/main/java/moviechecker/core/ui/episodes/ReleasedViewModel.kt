package moviechecker.core.ui.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import moviechecker.core.di.database.episode.Episode
import moviechecker.core.di.database.episode.EpisodeRepository
import javax.inject.Inject

@HiltViewModel
class ReleasedViewModel @Inject constructor(repository: EpisodeRepository) : ViewModel(), EpisodesViewModel {

    override val episodes: LiveData<List<Episode>> = repository.released.asLiveData()
}