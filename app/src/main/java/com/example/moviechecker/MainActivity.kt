package com.example.moviechecker

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.moviechecker.databinding.ActivityMainBinding
import com.example.moviechecker.ui.main.TabsAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            Snackbar.make(view, "Check for new episodes", Snackbar.LENGTH_LONG)
                .setAction("Check") {
                    // TODO: retrieve data
                }.show()
        }
    }
}