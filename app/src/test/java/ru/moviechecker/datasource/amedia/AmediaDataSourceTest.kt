package ru.moviechecker.datasource.amedia

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import ru.moviechecker.datasource.AmediaDataSource
import ru.moviechecker.datasource.model.DataState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class AmediaDataSourceTest {

    @BeforeTest
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
        every { Log.e(any(), any(), any(Throwable::class)) } answers {
            println("ERROR: ${it.invocation.args[0]}: ${it.invocation.args[1]}")
            0
        }
    }

    @Test
    fun retrieveData() {
        val sourceData =
            AmediaDataSource().retrieveData(javaClass.getResource("/amedia/amedia.html")!!.toURI())
        assertEquals("Animedia Online", sourceData.site.title)
        assertEquals(
            18,
            sourceData.entries.size,
            "Количество полученных записей не соответствует ожиданию"
        )

        val mojDjejmon = sourceData.entries.firstOrNull { it.movie.pageId == "moj-djejmon" }
        assertNotNull(mojDjejmon, "Запись \"Мой Дэймон\" не найдена")
        assertEquals("Мой Дэймон", mojDjejmon.movie.title)
        assertEquals(1, mojDjejmon.season?.number)
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

        val nevestaCharodeja =
            sourceData.entries.firstOrNull { it.movie.pageId == "nevesta-charodeja" }
        assertNotNull(nevestaCharodeja, "Запись \"Невеста чародея\" не найдена")
        assertEquals("Невеста чародея", nevestaCharodeja.movie.title)
        assertEquals(2, nevestaCharodeja.season?.number)
        assertEquals("/1362-nevesta-charodeja-2.html", nevestaCharodeja.season?.link)
        assertEquals(
            LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(21, 51)),
            nevestaCharodeja.episode?.date
        )

        val ubijcaGoblinov = sourceData.entries.firstOrNull { it.movie.pageId == "ubijca-goblinov" }
        assertNotNull(ubijcaGoblinov, "Запись \"Убийца гоблинов\" не найдена")
        assertEquals(DataState.EXPECTED, ubijcaGoblinov.episode?.state)
        assertEquals(
            LocalDateTime.of(LocalDate.now(), LocalTime.of(19, 0)),
            ubijcaGoblinov.episode?.date
        )

        val klevatess = sourceData.entries.firstOrNull { it.movie.pageId == "klevatess" }
        assertNotNull(klevatess, "Запись \"Клеватесс\" не найдена")
        assertEquals("Клеватесс", klevatess.movie.title)
        assertEquals(2, klevatess.season?.number)
        assertEquals("Король демонических зверей и легенда о ложном герое", klevatess.season?.title)
        assertEquals(DataState.RELEASED, klevatess.episode?.state)
        assertEquals(
            LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(16, 18)),
            klevatess.episode?.date
        )

        val adskijRezhim = sourceData.entries.firstOrNull { it.movie.pageId == "adskij-rezhim-gejmer-kotoryj-ljubit-spidran-stanovitsja-bespodobnym-v-parallelnom-mire-s-ustarevshimi-nastrojkami" }
        assertNotNull(adskijRezhim, "Запись \"Адский режим\" не найдена")
        assertEquals("Адский режим", adskijRezhim.movie.title)
        assertEquals(2, adskijRezhim.season?.number)
        assertEquals("Геймер, который любит спидран, становится бесподобным в параллельном мире с устаревшими настройками", adskijRezhim.season?.title)
        assertEquals(DataState.RELEASED, adskijRezhim.episode?.state)
        assertEquals(
            LocalDateTime.of(LocalDate.of(2026, 8, 14), LocalTime.of(19, 16)),
            adskijRezhim.episode?.date
        )

        // TODO: найти более старые записи чем вчера и выходящие нестабильно
    }
}