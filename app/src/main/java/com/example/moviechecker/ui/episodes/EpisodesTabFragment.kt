package com.example.moviechecker.ui.episodes

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.moviechecker.CheckerApplication
import com.example.moviechecker.R
import com.example.database.episode.EpisodeDetail
import com.example.datasource.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class EpisodesTabFragment() : Fragment(R.layout.fragment_tab) {

    protected val episodesViewModel: EpisodesViewModel by activityViewModels { EpisodesViewModel.Factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefresh.setOnRefreshListener {
            onRefresh(swipeRefresh)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val database = (activity?.application as CheckerApplication).database
        recyclerView.adapter = EpisodesAdapter(EpisodesController(database))

        getData().observe(viewLifecycleOwner) { records ->
            records.let { list ->
                (recyclerView.adapter as EpisodesAdapter).submitList(list)
            }
        }

    }

    fun onRefresh(swipeRefresh: SwipeRefreshLayout) {
        // начинаем показывать прогресс
        swipeRefresh.isRefreshing = true
        lifecycleScope.launch(Dispatchers.IO) {
            (activity?.application as CheckerApplication).database.populateDatabase(DataProvider.retrieveData())
        }
        // прячем прогресс
        swipeRefresh.isRefreshing = false
    }

    abstract fun getData(): LiveData<List<EpisodeDetail>>

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("TEST", "(onSaveInstanceState) $this")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TEST", "(onCreate) $this")
    }

    override fun onStart() {
        super.onStart()
        Log.i("TEST", "(onStart) $this")
    }

    override fun onResume() {
        super.onResume()
        Log.i("TEST", "(onResume) $this")
    }

    override fun onPause() {
        super.onPause()
        Log.i("TEST", "(onPause) $this")
    }

    override fun onStop() {
        super.onStop()
        Log.i("TEST", "(onStop) $this")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("TEST", "(onDestroy) $this")
    }

}