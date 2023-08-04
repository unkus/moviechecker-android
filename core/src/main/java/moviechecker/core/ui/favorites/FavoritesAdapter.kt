package moviechecker.core.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import moviechecker.core.R
import moviechecker.core.di.database.favorite.Favorite

class FavoritesAdapter(private val controller: FavoritesController): ListAdapter<Favorite, FavoritesAdapter.FavoriteViewHolder>(
    FavoriteComparator()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder = FavoriteViewHolder.create(parent)

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)

        holder.itemView.setOnClickListener {
            controller.onOpenClicked(holder.itemView.context, current)
        }

        holder.itemView.findViewById<Button>(R.id.forgotButton).setOnClickListener {
            controller.onForgotClicked(current.siteAddress, current.moviePageId)
        }
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)

        fun bind(favorite: Favorite?) {
            favorite?.let {
                titleTextView.text = it.title
            }
        }

        companion object {
            fun create(parent: ViewGroup): FavoriteViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_favorite, parent, false)
                return FavoriteViewHolder(view)
            }
        }
    }

    class FavoriteComparator : DiffUtil.ItemCallback<Favorite>() {
        override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem.siteAddress == newItem.siteAddress
                    && oldItem.moviePageId == newItem.moviePageId
        }
    }
}