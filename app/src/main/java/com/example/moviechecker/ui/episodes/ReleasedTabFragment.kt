package com.example.moviechecker.ui.episodes

import androidx.lifecycle.LiveData
import com.example.moviechecker.model.State
import com.example.moviechecker.model.episode.EpisodeDetail

class ReleasedTabFragment : EpisodesTabFragment(State.RELEASED) {

    override fun getData(): LiveData<List<EpisodeDetail>> = episodesViewModel.loadReleased()
}