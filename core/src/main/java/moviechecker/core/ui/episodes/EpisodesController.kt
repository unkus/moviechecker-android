package moviechecker.core.ui.episodes

import moviechecker.core.ui.ItemController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moviechecker.core.di.database.DataService
import moviechecker.core.di.database.episode.Episode

class EpisodesController(private val dataService: DataService) : ItemController() {

    fun onFavoriteChecked(episode: Episode) = CoroutineScope(Dispatchers.IO).launch {
        dataService.addToFavorites(episode.siteAddress, episode.moviePageId)
    }

    fun onFavoriteUnchecked(episode: Episode) = CoroutineScope(Dispatchers.IO).launch {
        dataService.removeFromFavorites(episode.siteAddress, episode.moviePageId)
    }

    fun setViewed(episode: Episode) {
        runBlocking(Dispatchers.IO) {
            dataService.markEpisodeViewed(episode);
        }
    }
}