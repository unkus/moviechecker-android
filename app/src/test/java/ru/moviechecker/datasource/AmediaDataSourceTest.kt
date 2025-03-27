package ru.moviechecker.datasource

import android.util.Log
import io.mockk.EqMatcher
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import ru.moviechecker.datasource.model.DataState
import ru.moviechecker.datasource.model.SiteData
import java.net.URI
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal class AmediaDataSourceTest {

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
        mockkConstructor(SiteData::class)

        every { constructedWith<SiteData>(EqMatcher(URI.create("https://amedia.lol"))).address } returns URI.create(
            javaClass.getResource("/amedia/amedia.html")!!.toString()
        )

        val records = AmediaDataSource().retrieveData()
        assertEquals("Количество полученных записей не соответствует ожиданию", 16, records.size)
        val mojDjejmon = records.firstOrNull { it.movie.pageId == "moj-djejmon" }
        assertNotNull("Запись \"Мой Дэймон\" не найдена", mojDjejmon)
        assertEquals("Мой Дэймон", mojDjejmon!!.movie.title)
        assertEquals(1, mojDjejmon.season?.number)
        assertEquals("Мой Дэймон", mojDjejmon.season?.title)
        assertEquals("/1593-moj-djejmon.html", mojDjejmon.season?.link)
        assertEquals(
            "/uploads/posts/2023-12/thumbs/fhk1xclgnqldcwyf__6895b8df64dcf260929c7c58a83a81e7.webp",
            mojDjejmon.season?.posterLink
        )
        assertEquals(13, mojDjejmon.episode?.number)
        assertEquals(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 13)),
            mojDjejmon.episode?.date
        )
        assertEquals(DataState.RELEASED, mojDjejmon.episode?.state)
        assertEquals("/1593-moj-djejmon/episode/13/seriya-onlayn.html", mojDjejmon.episode?.link)

        val nevestaCharodeja = records.firstOrNull { it.movie.pageId == "nevesta-charodeja" }
        assertNotNull("Запись \"Невеста чародея\" не найдена", nevestaCharodeja)
        assertEquals("Невеста чародея", nevestaCharodeja!!.movie.title)
        assertEquals(2, nevestaCharodeja.season?.number)
        assertEquals("Невеста чародея 2", nevestaCharodeja.season?.title)
        assertEquals("/1362-nevesta-charodeja-2.html", nevestaCharodeja.season?.link)
        assertEquals(
            LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(21, 51)),
            nevestaCharodeja.episode?.date
        )

        val ubijcaGoblinov = records.firstOrNull { it.movie.pageId == "ubijca-goblinov" }
        assertNotNull("Запись \"Убийца гоблинов\" не найдена", ubijcaGoblinov)
        assertEquals(DataState.EXPECTED, ubijcaGoblinov!!.episode?.state)
        assertEquals(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(19, 0)),
            ubijcaGoblinov.episode?.date
        )
        // TODO: найти более старые записи чем вчера и выходящие нестабильно

    }
}