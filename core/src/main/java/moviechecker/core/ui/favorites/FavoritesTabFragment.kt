package moviechecker.core.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import moviechecker.core.CheckerApplication
import moviechecker.core.R

@AndroidEntryPoint
class FavoritesTabFragment : Fragment(R.layout.fragment_tab) {

    private val viewModel: FavoritesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter =
            FavoritesAdapter(FavoritesController(((activity?.application as CheckerApplication).dataService)))

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            favorites.let { list ->
                (recyclerView.adapter as FavoritesAdapter).submitList(list)
            }
        }
    }
}