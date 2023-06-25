package com.example.moviechecker.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moviechecker.ui.episodes.ExpectedTabFragment
import com.example.moviechecker.ui.episodes.ReleasedTabFragment
import com.example.moviechecker.ui.favorites.FavoritesTabFragment

class TabsAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when(position) {
        0 -> ReleasedTabFragment()
        1 -> ExpectedTabFragment()
        2 -> FavoritesTabFragment()
        else -> throw IllegalArgumentException("Illegal position")
    }

}