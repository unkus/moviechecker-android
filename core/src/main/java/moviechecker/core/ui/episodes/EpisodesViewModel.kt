package moviechecker.core.ui.episodes

import androidx.lifecycle.LiveData
import moviechecker.core.di.database.episode.Episode

interface EpisodesViewModel {

    val episodes: LiveData<List<Episode>>

}