package com.example.moviechecker.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.example.moviechecker.model.Linkable

abstract class ItemController {

    open fun onOpenClicked(context: Context, linkable: Linkable) {
        val browserIntent = Intent(Intent.ACTION_VIEW, linkable.link)
        ContextCompat.startActivity(context, browserIntent, null)
    }
}
