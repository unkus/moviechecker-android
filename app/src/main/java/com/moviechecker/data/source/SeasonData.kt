package com.moviechecker.data.source

import java.net.URI

data class SeasonData(
    val number: Int,
    val title: String? = null,
    val link: URI,
    val posterLink: URI? = null
) {
    data class Builder(
        private var number: Int? = null,
        private var title: String? = null,
        private var link: URI? = null,
        private var posterLink: URI? = null
    ) {

        fun number(value: Int) = apply { this.number = value }
        fun title(value: String) = apply { this.title = value }
        fun link(value: URI) = apply { this.link = value }
        fun posterLink(value: URI) = apply { this.posterLink = value }

        fun build(): SeasonData {
            return SeasonData(
                number!!,
                title,
                link!!,
                posterLink
            )
        }

        fun validate(): Boolean {
            return number != null && link != null
        }
    }
}