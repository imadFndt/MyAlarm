package com.fndt.alarm.domain

import android.os.SystemClock
import com.fndt.alarm.domain.dto.AlarmIntent
import com.fndt.alarm.domain.dto.AlarmItem
import com.fndt.alarm.domain.dto.NextAlarmItem
import com.fndt.alarm.domain.utils.INTENT_FIRE_ALARM
import com.fndt.alarm.domain.utils.INTENT_SNOOZE_ALARM
import com.fndt.alarm.domain.utils.INTENT_STOP_ALARM
import com.fndt.alarm.presentation.AlarmSetup
import io.mockk.*
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*
import kotlin.random.Random

class AlarmControlTest {
    private val random = Random(SystemClock.elapsedRealtime())

    private fun getRandomItem() = AlarmItem(
        time = random.nextLong(1439),
        name = "Alarm${random.nextInt()}",
        isActive = random.nextBoolean(),
        id = random.nextLong(),
        melody = "MelodyId"
    )

    @Test
    fun `Add and remove item logic with callbacks`() = runBlocking<Unit> {
        //GIVEN
        val repository = mockk<IRepository>()
        var repoCallback: IRepository.Callback? = null
        val controlCallback: AlarmDataUseCase.Callback = mockk(relaxed = true)
        val list = mutableListOf<AlarmItem>()
        val item = getRandomItem()
        val secondItem = getRandomItem()

        val slot = slot<IRepository.Callback>()
        coEvery { repository.setCallback(capture(slot)) } answers {
            repoCallback = slot.captured
        }

        val itemSlot = slot<AlarmItem>()
        coEvery { repository.addItem(capture(itemSlot)) } answers {
            list.add(itemSlot.captured)
            repoCallback?.onUpdateList(list)
        }
        coEvery { repository.removeItem(capture(itemSlot)) } answers {
            list.remove(itemSlot.captured)
            repoCallback?.onUpdateList(list)
        }

        val control = AlarmControl(
            alarmSetup = mockk(), serviceHandler = mockk(), wakelockProvider = mockk(), repository = repository
        )

        //WHEN
        control.addDataUseCaseCallback(controlCallback)

        control.addItem(item)
        control.addItem(secondItem)
        control.removeItem(item)

        control.removeDataUseCaseCallback(controlCallback)
        control.addItem(getRandomItem())

        //THEN
        verify(exactly = 3) { controlCallback.onUpdateAlarmList(list) }
    }

    @Test
    fun `WakeLock proxying test`() {
        //GIVEN
        val repository = mockk<IRepository>(relaxed = true)
        val wakelockProvider = mockk<IWakelockProvider>(relaxed = true)
        val control = AlarmControl(
            alarmSetup = mockk(),
            serviceHandler = mockk(),
            wakelockProvider = wakelockProvider,
            repository = repository
        )

        //WHEN
        control.acquireWakeLock()
        control.releaseWakeLock()

        //THEN
        verify(exactly = 1) { wakelockProvider.acquireServiceLock() }
        verify(exactly = 1) { wakelockProvider.releaseServiceLock() }
    }

    @Test
    fun `Clearing resources`() {
        //GIVEN
        val repository = mockk<IRepository>(relaxed = true)
        val wakelockProvider = mockk<IWakelockProvider>(relaxed = true)
        val control = AlarmControl(
            alarmSetup = mockk(),
            serviceHandler = mockk(),
            wakelockProvider = wakelockProvider,
            repository = repository
        )
        //WHEN
        control.clear()

        //THEN
        verify(exactly = 1) {
            wakelockProvider.releaseServiceLock()
            repository.setCallback(null)
            repository.clear()
        }
    }

    @Test
    fun `Updating alarming item`() {
        //GIVEN
        val repository = mockk<IRepository>(relaxed = true)
        val controlCallback: AlarmDataUseCase.Callback = mockk(relaxed = true)
        val control = AlarmControl(
            alarmSetup = mockk(), serviceHandler = mockk(), wakelockProvider = mockk(), repository = repository
        )

        //WHEN
        control.addDataUseCaseCallback(controlCallback)
        control.requestAlarmingItem()
        control.removeDataUseCaseCallback(controlCallback)

        //THEN
        verify(exactly = 1) { controlCallback.onUpdateAlarmingItem(any()) }
    }

    @Test
    fun `Firing and stopping alarm`() {
        //GIVEN
        val item = getRandomItem().also { it.isActive = true }
        val repository = mockk<IRepository>(relaxed = true)
        val serviceHandler = mockk<IServiceHandler>(relaxed = true)
        val controlCallback: AlarmDataUseCase.Callback = mockk(relaxed = true)

        val control = AlarmControl(
            alarmSetup = mockk(),
            serviceHandler = serviceHandler,
            wakelockProvider = mockk(),
            repository = repository
        )
        control.addDataUseCaseCallback(controlCallback)

        //WHEN
        control.handleEvent(AlarmIntent(item, INTENT_FIRE_ALARM))
        control.handleEvent(AlarmIntent(item, INTENT_STOP_ALARM))

        //THEN
        verify(exactly = 1) {
            controlCallback.onUpdateAlarmingItem(item)
            serviceHandler.startService(INTENT_FIRE_ALARM, item)
            controlCallback.onUpdateAlarmingItem(null)
            serviceHandler.startService(INTENT_STOP_ALARM, item)
        }
        assertEquals(false, item.isActive)
    }

    @Test
    fun `Firing and snoozing alarm`() {
        //GIVEN
        val item = getRandomItem().also { it.isActive = true }
        val repository = mockk<IRepository>(relaxed = true)
        val serviceHandler = mockk<IServiceHandler>(relaxed = true)
        val controlCallback: AlarmDataUseCase.Callback = mockk(relaxed = true)

        val control = AlarmControl(
            alarmSetup = mockk(),
            serviceHandler = serviceHandler,
            wakelockProvider = mockk(),
            repository = repository
        )
        control.addDataUseCaseCallback(controlCallback)

        //WHEN
        control.handleEvent(AlarmIntent(item, INTENT_FIRE_ALARM))
        control.handleEvent(AlarmIntent(item, INTENT_SNOOZE_ALARM))

        //THEN
        verify(exactly = 1) {
            controlCallback.onUpdateAlarmingItem(item)
            serviceHandler.startService(INTENT_FIRE_ALARM, item)
            controlCallback.onUpdateAlarmingItem(null)
            serviceHandler.startService(INTENT_STOP_ALARM, item)
        }
    }

    @Test
    fun `Empty intent`() {
        //GIVEN
        val serviceHandler = mockk<IServiceHandler>(relaxed = true)
        val control = AlarmControl(
            alarmSetup = mockk(),
            serviceHandler = serviceHandler,
            wakelockProvider = mockk(),
            repository = mockk(relaxed = true)
        )

        //WHEN
        control.handleEvent(AlarmIntent(null, INTENT_FIRE_ALARM))
        control.handleEvent(AlarmIntent(null, INTENT_SNOOZE_ALARM))
        control.handleEvent(AlarmIntent(null, INTENT_STOP_ALARM))

        //THEN
        verify(exactly = 0) { serviceHandler.startService(any(), any()) }
    }

    @Test
    fun `Next alarm item calls and alarm setup`() {
        //GIVEN
        val repository = mockk<IRepository>(relaxed = true)
        val alarmSetup = mockk<AlarmSetup>(relaxed = true)

        var repoCallback: IRepository.Callback? = null
        val controlCallback: AlarmDataUseCase.Callback = mockk(relaxed = true)
        val nextAlarmItem = NextAlarmItem(getRandomItem(), Calendar.getInstance())

        val slot = slot<IRepository.Callback>()
        every { repository.setCallback(capture(slot)) } answers {
            repoCallback = slot.captured
        }

        val control = AlarmControl(
            alarmSetup = alarmSetup, serviceHandler = mockk(), wakelockProvider = mockk(), repository = repository
        )

        //WHEN
        control.addDataUseCaseCallback(controlCallback)
        repoCallback?.onUpdateNextItem(nextAlarmItem)
        repoCallback?.onUpdateNextItem(null)
        repoCallback?.onUpdateNextItem(null)

        //THEN
        verify(exactly = 1) {
            controlCallback.onUpdateNextItem(nextAlarmItem)
            alarmSetup.setAlarm(nextAlarmItem)
            alarmSetup.cancelAlarm()
        }
        verify(exactly = 2) {
            controlCallback.onUpdateNextItem(null)
        }
    }
}