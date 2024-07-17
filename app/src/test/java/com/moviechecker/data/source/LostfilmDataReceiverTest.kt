package com.moviechecker.data.source

import android.util.Log
import com.moviechecker.data.LostfilmDataSource
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.net.URI

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