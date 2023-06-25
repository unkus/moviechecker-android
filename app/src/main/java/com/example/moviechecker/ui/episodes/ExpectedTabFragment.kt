package com.example.moviechecker.ui.episodes

import androidx.lifecycle.LiveData
import com.example.moviechecker.model.State
import com.example.moviechecker.model.episode.EpisodeDetail

class ExpectedTabFragment : EpisodesTabFragment(State.EXPECTED) {

    override fun getData(): LiveData<List<EpisodeDetail>> = episodesViewModel.loadExpected()

}