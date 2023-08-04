package moviechecker.core.ui.episodes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import moviechecker.core.R
import moviechecker.core.di.database.episode.Episode
import moviechecker.core.di.State
import java.time.LocalDate


class EpisodesAdapter(private val controller: EpisodesController) : ListAdapter<Episode, EpisodesAdapter.EpisodeViewHolder>(
    EpisodeComparator()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder = EpisodeViewHolder.create(parent)

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener {
            controller.onOpenClicked(it.context, current)
            controller.setViewed(current)
        }

        holder.itemView.findViewById<CheckBox>(R.id.favoriteFlag).setOnCheckedChangeListener { buttonView, isChecked ->
            if(buttonView.isPressed) {
                if (isChecked) {
                    controller.onFavoriteChecked(current)
                } else {
                    controller.onFavoriteUnchecked(current)
                }
            }
        }
    }

    class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.date)
        private val titleCheckBox: CheckBox = itemView.findViewById(R.id.favoriteFlag)
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val episodeTextView: TextView = itemView.findViewById(R.id.episode_number)

        fun bind(episode: Episode?) {
            episode?.let {
                if(it.date.isEqual(LocalDate.now().atStartOfDay())) {
                    dateTextView.text = itemView.context.getString(R.string.date_today, itemView.context.getString(R.string.date_unstable))
                } else if(it.date.isAfter(LocalDate.now().plusDays(2).atStartOfDay())) {
                    dateTextView.text = it.date.toString()
                } else if(it.date.isAfter(LocalDate.now().plusDays(1).atStartOfDay())) {
                    dateTextView.text = itemView.context.getString(R.string.date_tomorrow, it.date.toLocalTime())
                } else if(it.date.isAfter(LocalDate.now().atStartOfDay())) {
                    dateTextView.text = itemView.context.getString(R.string.date_today, it.date.toLocalTime())
                } else if(it.date.isAfter(LocalDate.now().minusDays(1).atStartOfDay())) {
                    dateTextView.text = itemView.context.getString(R.string.date_yesterday, it.date.toLocalTime())
                } else {
                    dateTextView.text = it.date.toString()
                }

                titleCheckBox.isChecked = it.isInFavorite
                if (it.seasonNumber > 1) {
                    val movieTitle = "${it.movieTitle} ${it.seasonNumber}"
                    titleTextView.text = movieTitle
                } else {
                    titleTextView.text = it.movieTitle
                }
                if(it.state == State.VIEWED) {
                    val episodeTitle = "${it.title} ✓"
                    episodeTextView.text = episodeTitle
                } else {
                    episodeTextView.text = it.title
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): EpisodeViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_episodes, parent, false)
                return EpisodeViewHolder(view)
            }
        }
    }

    class EpisodeComparator : DiffUtil.ItemCallback<Episode>() {
        override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean = oldItem === newItem

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
            return oldItem.siteAddress == newItem.siteAddress
                    && oldItem.moviePageId == newItem.moviePageId
                    && oldItem.seasonNumber == newItem.seasonNumber
                    && oldItem.number == newItem.number
        }
    }
}