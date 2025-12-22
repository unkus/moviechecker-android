package ru.moviechecker.datasource

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import ru.moviechecker.datasource.model.DataState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal class LostfilmDataSourceTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)

        every { Log.i(any(), any()) } answers {
            println("INFO: ${it.invocation.args[0]}: ${it.invocation.args[1]}")
            0
        }
        every { Log.d(any(), any()) } answers {
            println("DEBUG: ${it.invocation.args[0]}: ${it.invocation.args[1]}")
            0
        }
        every { Log.e(any(), any()) } answers {
            println("ERROR: ${it.invocation.args[0]}: ${it.invocation.args[1]}")
            0
        }
    }

    @Test
    fun retrieveData() {
        val dataSource = spyk(LostfilmDataSource(), recordPrivateCalls = true)
        val address = javaClass.getResource("/lostfilm/lostfilm.html")!!.toURI()

        // заглушки
        every {
            dataSource invoke "resolveLink" withArguments listOf(address, any() as String)
        } answers { javaClass.getResource("/lostfilm${secondArg<String>()}.html") }

        val sourceData = dataSource.retrieveData(address)
        assertEquals("LostFilm.TV", sourceData.site.title)
        assertEquals(
            "Количество полученных записей не соответствует ожиданию",
            4,
            sourceData.entries.size
        )
        val mufasaTheLionKing =
            sourceData.entries.firstOrNull { it.movie.pageId == "Mufasa_The_Lion_King" }
        assertNotNull("Запись \"Муфаса: Король Лев\" не найдена", mufasaTheLionKing)
        assertEquals("Муфаса: Король Лев", mufasaTheLionKing!!.movie.title)
        assertEquals("/movies/Mufasa_The_Lion_King", mufasaTheLionKing.movie.link)
        assertEquals(
            "/Static/Images/878/Posters/poster.jpg",
            mufasaTheLionKing.movie.posterLink
        )
        assertNull(mufasaTheLionKing.season)
        assertNull(mufasaTheLionKing.episode)

        val euphoria = sourceData.entries.firstOrNull { it.movie.pageId == "Euphoria" }
        assertNotNull("Запись \"Эйфория\" не найдена", euphoria)
        assertEquals("Эйфория", euphoria!!.movie.title)
        assertEquals("/series/Euphoria", euphoria.movie.link)
        assertEquals(
            "/Static/Images/915/Posters/e_999_1.jpg",
            euphoria.movie.posterLink
        )
        assertEquals(999, euphoria.season?.number)
        assertNull(euphoria.season?.title)
        assertEquals("/series/Euphoria/additional", euphoria.season?.link)
        assertEquals(
            "/Static/Images/915/Posters/icon.jpg",
            euphoria.season?.posterLink
        )
        assertEquals(1, euphoria.episode?.number)
        assertEquals("Беды не длятся вечно", euphoria.episode?.title)
//        assertEquals(
//            "После срыва Ру проводит канун Рождества в закусочной вместе с Али и признается, что чувствует себя виноватой из-за отношения к своей матери. Али рассказывает свою историю зависимости и предлагает Ру простить себя за ошибки, чтобы стать лучше.",
//            euphoria.episode?.description
//        )
        assertEquals("/series/Euphoria/additional/episode_1", euphoria.episode?.link)
        assertEquals(DataState.RELEASED, euphoria.episode?.state)
        assertEquals(
            LocalDateTime.of(LocalDate.of(2025, 2, 24), LocalTime.MIN),
            euphoria.episode?.date
        )

        val severance = sourceData.entries.firstOrNull { it.movie.pageId == "Severance" }
        assertNotNull("Запись \"Разделение\" не найдена", severance)
        assertEquals("Разделение", severance!!.movie.title)
        assertEquals("/series/Severance", severance.movie.link)
        assertEquals(
            "/Static/Images/651/Posters/e_2_6.jpg",
            severance.movie.posterLink
        )
        assertEquals(2, severance.season?.number)
        assertNull(severance.season?.title)
        assertEquals("/series/Severance/season_2", severance.season?.link)
        assertEquals(
            "/Static/Images/651/Posters/icon_s2.jpg",
            severance.season?.posterLink
        )
        assertEquals(6, severance.episode?.number)
        assertEquals("Аттила", severance.episode?.title)
//        assertEquals(
//            "Марк и Хелли узнают, что Дилан обнаружил инструкцию Ирвинга, в то время как Милчик хочет разобраться с нарушениями в оценке производительности и оставляет мисс Хуан на месте руководителя. Хелли и Марка разбираются в своих отношениях, а Ирвинг навещает Бёрта у него дома.",
//            severance.episode?.description
//        )
        assertEquals("/series/Severance/season_2/episode_6", severance.episode?.link)
        assertEquals(DataState.RELEASED, severance.episode?.state)
        assertEquals(
            LocalDateTime.of(LocalDate.of(2025, 2, 23), LocalTime.MIN),
            severance.episode?.date
        )

        val surrealEstate = sourceData.entries.firstOrNull { it.movie.pageId == "SurrealEstate" }
        assertNotNull("Запись \"Сюрриэлторы\" не найдена", surrealEstate)
        assertEquals("Сюрриэлторы", surrealEstate!!.movie.title)
        assertEquals("/series/SurrealEstate", surrealEstate.movie.link)
        assertEquals("/series/SurrealEstate/season_3/episode_3", surrealEstate.episode?.link)
    }
}