package ru.moviechecker.datasource.model

import android.util.Log
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.moviechecker.datasource.AmediaDataSource
import java.net.URI
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals

class AmediaDataReceiverTest {

    @BeforeTest
    fun setUp() {
        mockkStatic(Log::class)

        every { Log.i(any(), any()) } returns 0
    }

    @Test
    fun receivingData() {
        mockkConstructor(SiteData::class)

        every { anyConstructed<SiteData>().address } returns URI.create(javaClass.getResource("amedia/amedia.html")!!.toString())

        CoroutineScope(Dispatchers.IO).launch {
            val sourceData = AmediaDataSource().retrieveData(
                uri = URI.create("")
            )
            assertNotEquals(0, sourceData.entries.size)
            sourceData.entries.forEach { r -> println(r) }
        }
    }

}