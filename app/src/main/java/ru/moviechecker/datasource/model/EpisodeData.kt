package ru.moviechecker.datasource.model

import java.time.LocalDateTime

data class EpisodeData(
    val number: Int,
    val title: String?,
    val link: String,
    val state: DataState,
    val date: LocalDateTime
) {
    data class Builder(
        private var number: Int? = null,
        private var title: String? = null,
        private var link: String? = null,
        private var state: DataState? = null,
        private var date: LocalDateTime? = null
    ) {

        fun number(value: Int) = apply { this.number = value }
        fun title(value: String) = apply { this.title = value }
        fun link(value: String) = apply { this.link = value }
        fun state(value: DataState) = apply { this.state = value }
        fun date(value: LocalDateTime) = apply { this.date = value }

        fun build(): EpisodeData {
            return EpisodeData(
                number!!,
                title,
                link!!,
                state!!,
                date!!
            )
        }

        fun validate(): Boolean {
            return number != null && link != null && state != null && date != null
        }

    }
}