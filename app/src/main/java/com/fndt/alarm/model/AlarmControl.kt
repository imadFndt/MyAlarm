package com.fndt.alarm.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fndt.alarm.model.db.AlarmItemDao
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class AlarmControl @Inject constructor(
    private val alarmItemDao: AlarmItemDao
) {
    lateinit var rxAlarmList: Observable<List<AlarmItem>>
    val alarmList: LiveData<List<AlarmItem>> get() = alarmListData
    private val alarmListData: MutableLiveData<List<AlarmItem>> = MutableLiveData()
    var alarm: (() -> Unit)? = null

    init {
        //TODO REMOVE
        val items = mutableListOf<AlarmItem>()
        for (i in 0..5) {
            items.add(
                AlarmItem(
                    i * 60 + Random(1).nextLong() % 60,
                    "ALORM",
                    Random(2).nextBoolean()
                )
            )
        }
        rxAlarmList = Observable.just(items)
        alarmListData.value = items
    }

    fun setUpAlarm(alarmItem: AlarmItem) {
        TODO()
    }

    fun cancelAlarm(alarmItem: AlarmItem) {
        TODO()
    }

    fun changeAlarm(alarmItem: AlarmItem) {
        TODO()
    }
}