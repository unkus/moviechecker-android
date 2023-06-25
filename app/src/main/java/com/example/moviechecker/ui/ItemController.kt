package com.example.moviechecker.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat

abstract class ItemController {

    open fun onOpenClicked(context: Context, link: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, link)
        ContextCompat.startActivity(context, browserIntent, null)
    }
}