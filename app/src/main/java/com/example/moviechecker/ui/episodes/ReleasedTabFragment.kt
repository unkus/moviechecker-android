package com.example.moviechecker.ui.episodes

import androidx.lifecycle.LiveData
import com.example.database.episode.EpisodeDetail

class ReleasedTabFragment : EpisodesTabFragment() {

    override fun getData(): LiveData<List<EpisodeDetail>> = episodesViewModel.loadReleased()
}