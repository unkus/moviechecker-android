package moviechecker.core.ui.episodes

class ReleasedTabFragment : EpisodesTabFragment() {

    override fun getData() = episodesViewModel.released
}