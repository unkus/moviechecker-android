package moviechecker.core.ui.episodes

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moviechecker.core.CheckerApplication
import moviechecker.core.DataRetrieveWorker
import moviechecker.core.R
import moviechecker.core.di.database.episode.Episode

@AndroidEntryPoint
abstract class EpisodesTabFragment : Fragment(R.layout.fragment_tab) {

    protected val episodesViewModel: EpisodesViewModel by viewModels()
//    protected val episodesViewModel: EpisodesViewModel by activityViewModels { EpisodesViewModel.Factory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefresh.setOnRefreshListener {
            onRefreshClicked(swipeRefresh)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter =
            EpisodesAdapter(EpisodesController((activity?.application as CheckerApplication).dataService))

        getData().observe(viewLifecycleOwner) { records ->
            records.let { list ->
                (recyclerView.adapter as EpisodesAdapter).submitList(list)
            }
        }

    }

    fun onRefreshClicked(swipeRefresh: SwipeRefreshLayout) {
        // начинаем показывать прогресс
        swipeRefresh.isRefreshing = true
        lifecycleScope.launch(Dispatchers.IO) {
//            (activity?.application as CheckerApplication).dataService.checkSources()
            val workRequest = OneTimeWorkRequestBuilder<DataRetrieveWorker>().build()
            activity?.let { WorkManager.getInstance(it.application).enqueue(workRequest) }
        }
        // прячем прогресс
        swipeRefresh.isRefreshing = false
    }

    abstract fun getData(): LiveData<List<Episode>>

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