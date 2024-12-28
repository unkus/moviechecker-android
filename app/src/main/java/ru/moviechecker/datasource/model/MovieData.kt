package ru.moviechecker.datasource.model

data class MovieData (
    val pageId: String,
    val title: String,
    val link: String?,
    val posterLink: String? = null
) {
    data class Builder(
        private var pageId: String? = null,
        private var title: String? = null,
        private var link: String? = null,
        private var posterLink: String? = null
    ) {

        fun pageId(value: String) = apply { this.pageId = value }
        fun title(value: String) = apply { this.title = value }
        fun link(value: String) = apply { this.link = value }
        fun posterLink(value: String) = apply { this.posterLink = value }

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