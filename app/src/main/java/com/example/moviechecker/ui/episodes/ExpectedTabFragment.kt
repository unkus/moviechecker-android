package com.example.moviechecker.ui.episodes

import androidx.lifecycle.LiveData
import com.example.database.episode.EpisodeDetail

class ExpectedTabFragment : EpisodesTabFragment() {

    override fun getData(): LiveData<List<EpisodeDetail>> = episodesViewModel.loadExpected()

}