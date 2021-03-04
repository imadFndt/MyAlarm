package com.fndt.alarm.domain.dto

import android.os.SystemClock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.random.Random

class AlarmItemTest {
    @Test
    fun `Serializers works correctly`() {
        val random = Random(SystemClock.elapsedRealtime())
        val expected =
            AlarmItem(
                time = random.nextLong(1439),
                name = "Alarm${random.nextInt()}",
                isActive = random.nextBoolean(),
                id = random.nextLong(),
                melody = "MelodyId"
            )
        val itemArray = expected.toByteArray()
        val actual = AlarmItem.fromByteArray(itemArray)
        assertEquals(expected, actual)
    }

    @Test
    fun `Crash when time more than day`() {
        val random = Random(SystemClock.elapsedRealtime())
        assertThrows(IllegalStateException::class.java) {
            AlarmItem(
                time = random.nextLong(1439, 10000),
                name = "Alarm${random.nextInt()}",
                isActive = random.nextBoolean(),
                id = random.nextLong(),
                melody = "MelodyId"
            )
        }
    }
}
