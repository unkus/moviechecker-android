package ru.moviechecker.datasource.model

data class SeasonData(
    val number: Int,
    val title: String? = null,
    val link: String,
    val posterLink: String? = null
) {
    data class Builder(
        private var number: Int? = null,
        private var title: String? = null,
        private var link: String? = null,
        private var posterLink: String? = null
    ) {

        fun number(value: Int) = apply { this.number = value }
        fun title(value: String) = apply { this.title = value }
        fun link(value: String) = apply { this.link = value }
        fun posterLink(value: String) = apply { this.posterLink = value }

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