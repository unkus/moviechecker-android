package moviechecker.core.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import moviechecker.core.di.Linkable

abstract class ItemController {

    open fun onOpenClicked(context: Context, linkable: Linkable) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkable.link.toString()))
        ContextCompat.startActivity(context, browserIntent, null)
    }
}
