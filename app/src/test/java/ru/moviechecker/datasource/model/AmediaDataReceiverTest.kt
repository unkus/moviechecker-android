package ru.moviechecker.datasource.model

import android.util.Log
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import ru.moviechecker.datasource.AmediaDataSource
import java.net.URI

class AmediaDataReceiverTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)

        every { Log.i(any(), any()) } returns 0
    }

    @Test
    fun receivingData() {
        mockkConstructor(SiteData::class)

        every { anyConstructed<SiteData>().address } returns URI.create(javaClass.getResource("/amedia.html")!!.toString())

        CoroutineScope(Dispatchers.IO).launch {
            val records = AmediaDataSource().retrieveData()
            assertNotEquals(0, records.size)
            records.forEach { r -> println(r) }
        }
    }

}