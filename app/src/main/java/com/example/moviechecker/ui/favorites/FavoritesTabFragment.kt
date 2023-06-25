package com.example.moviechecker.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.moviechecker.R

class FavoritesTabFragment : Fragment(R.layout.fragment_tab) {
    private val viewModel: FavoritesViewModel by viewModels { FavoritesViewModel.Factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = FavoritesAdapter(FavoritesController((viewModel)))

        viewModel.favoritesDetailed.observe(viewLifecycleOwner) { favorites ->
            favorites.let { list ->
                (recyclerView.adapter as FavoritesAdapter).submitList(list)
            }
        }
    }
}