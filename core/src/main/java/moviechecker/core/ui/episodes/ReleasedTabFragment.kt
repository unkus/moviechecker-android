package moviechecker.core.ui.episodes

import androidx.fragment.app.viewModels

class ReleasedTabFragment : EpisodesTabFragment() {

    override val viewModel: ReleasedViewModel by viewModels()

}