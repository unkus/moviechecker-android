package moviechecker.core

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moviechecker.core.databinding.ActivityMainBinding
import moviechecker.core.ui.main.TabsAdapter
import java.sql.Time
import java.time.Duration
import java.time.Instant


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startService(Intent(this, DataReceiverService::class.java))

//        val workRequest = OneTimeWorkRequestBuilder<DataRetrieveWorker>().build()
//        val workRequest = PeriodicWorkRequestBuilder<DataRetrieveWorker>(Duration.ofMinutes(30)).build()
//        WorkManager.getInstance(applicationContext).enqueue(workRequest)

//        val jobService = ComponentName(application, DataReceiverService::class.java)
//        val jobScheduler: JobScheduler = application.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
//        jobScheduler.schedule(JobInfo.Builder(0, jobService).setPeriodic(1000*60*60).build())

        // Create view pager
        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = TabsAdapter(supportFragmentManager, lifecycle)

        // Attach TabLayout with ViewPager2
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> resources.getString(R.string.title_latest_episodes_tab)
                1 -> resources.getString(R.string.title_expected_episodes_tab)
                2 -> resources.getString(R.string.title_favorites_tab)
                else -> throw IllegalArgumentException("Illegal position")
            }
        }.attach()

        // Action button
        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Remove viewed episodes", Snackbar.LENGTH_LONG)
                .setAction("Cleanup") {
                    lifecycleScope.launch(Dispatchers.IO) {
                        (application as CheckerApplication).dataService.cleanupData()
                    }
                }.show()
        }
    }
}