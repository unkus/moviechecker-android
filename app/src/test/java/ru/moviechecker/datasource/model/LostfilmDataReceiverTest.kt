package ru.moviechecker.datasource.model

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import ru.moviechecker.datasource.LostfilmDataSource

class LostfilmDataReceiverTest {

    @Before
    fun setUp() {
        mockkStatic(Log::class)

        every { Log.i(any(), any()) } returns 0
    }

    @Test
    fun receivingData() {
        CoroutineScope(Dispatchers.IO).launch {
            val records = LostfilmDataSource().retrieveData()
            assertNotEquals(0, records.size)
            records.forEach { r -> println(r) }
        }
    }

}