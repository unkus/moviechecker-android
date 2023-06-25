package com.example.moviechecker.ui.episodes

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviechecker.R
import com.example.moviechecker.model.episode.EpisodeDetail
import java.time.LocalDate


class EpisodesAdapter(private val controller: EpisodesController) : ListAdapter<EpisodeDetail, EpisodesAdapter.EpisodeViewHolder>(
    EpisodeComparator()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        return EpisodeViewHolder.create(parent, controller)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class EpisodeViewHolder(itemView: View, private val controller: EpisodesController) : RecyclerView.ViewHolder(itemView) {
        private var siteAddress: Uri? = null
        private var moviePageId: String? = null
        private var link: Uri? = null
        private val dateTextView: TextView = itemView.findViewById(R.id.date)
        private val titleCheckBox: CheckBox = itemView.findViewById(R.id.favoriteFlag)
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val episodeTextView: TextView = itemView.findViewById(R.id.episode_number)

        init {
            itemView.setOnClickListener {
                link?.let {
                    controller.onOpenClicked(itemView.context, it)
                }
            }
            titleCheckBox.setOnCheckedChangeListener { compoundButton, isChecked ->
                if(compoundButton.isPressed) {
                    Log.i("TEST", "isChecked: $isChecked")
                    if (isChecked) {
                        controller.onFavoriteChecked(siteAddress, moviePageId)
                    } else {
                        controller.onFavoriteUnchecked(siteAddress, moviePageId)
                    }
                }
            }
        }

        fun bind(episode: EpisodeDetail?) {
            episode?.let {
                siteAddress = it.siteAddress
                moviePageId = it.moviePageId
                link = Uri.withAppendedPath(it.siteAddress, it.link.toString())

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
                titleTextView.text = it.movieTitle
                episodeTextView.text = it.title
            }
        }

        companion object {
            fun create(parent: ViewGroup, controller: EpisodesController): EpisodeViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_episodes, parent, false)
                return EpisodeViewHolder(view, controller)
            }
        }
    }

    class EpisodeComparator : DiffUtil.ItemCallback<EpisodeDetail>() {
        override fun areItemsTheSame(oldItem: EpisodeDetail, newItem: EpisodeDetail): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: EpisodeDetail, newItem: EpisodeDetail): Boolean {
            return oldItem.siteAddress == newItem.siteAddress
                    && oldItem.moviePageId == newItem.moviePageId
                    && oldItem.seasonNumber == newItem.seasonNumber
                    && oldItem.number == newItem.number
        }
    }
}