package com.example.moviechecker.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.database.Linkable

abstract class ItemController {

    open fun onOpenClicked(context: Context, linkable: Linkable) {
        val browserIntent = Intent(Intent.ACTION_VIEW, linkable.link)
        ContextCompat.startActivity(context, browserIntent, null)
    }
}
