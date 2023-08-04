package moviechecker.core.ui.episodes

class ExpectedTabFragment : EpisodesTabFragment() {

    override fun getData() = episodesViewModel.expected

}