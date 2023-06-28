package com.example.moviechecker.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moviechecker.R
import com.example.moviechecker.model.favorite.FavoriteDetail

class FavoritesAdapter(private val controller: FavoritesController): ListAdapter<FavoriteDetail, FavoritesAdapter.FavoriteViewHolder>(
    FavoriteComparator()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder.create(parent)
    }

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

        fun bind(favorite: FavoriteDetail?) {
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

    class FavoriteComparator : DiffUtil.ItemCallback<FavoriteDetail>() {
        override fun areItemsTheSame(oldItem: FavoriteDetail, newItem: FavoriteDetail): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: FavoriteDetail, newItem: FavoriteDetail): Boolean {
            return oldItem.siteAddress == newItem.siteAddress
                    && oldItem.moviePageId == newItem.moviePageId
        }
    }
}