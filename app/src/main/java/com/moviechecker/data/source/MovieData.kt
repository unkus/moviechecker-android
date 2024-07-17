package com.moviechecker.data.source

import java.net.URI

data class MovieData (
    val pageId: String,
    val title: String,
    val link: URI?,
    val posterLink: URI? = null
) {
    data class Builder(
        private var pageId: String? = null,
        private var title: String? = null,
        private var link: URI? = null,
        private var posterLink: URI? = null
    ) {

        fun pageId(value: String) = apply { this.pageId = value }
        fun title(value: String) = apply { this.title = value }
        fun link(value: URI) = apply { this.link = value }
        fun posterLink(value: URI) = apply { this.posterLink = value }

        fun build(): MovieData {
            return MovieData(
                pageId!!,
                title!!,
                link,
                posterLink
            )
        }

        fun validate(): Boolean {
            return pageId != null && title != null
        }
    }
}