package com.fndt.alarm.presentation.util

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import com.fndt.alarm.domain.dto.AlarmItem

class AlarmItemGestureListener : GestureDetector.OnGestureListener {
    var longPressedCallback: ((AlarmItem?) -> Unit)? = null
    var singleTapCallback: ((AlarmItem?) -> Unit)? = null

    var currentItem: AlarmItem? = null

    override fun onLongPress(e: MotionEvent?) {
        Log.d("AlarmItemGesture", "LongPress $currentItem")
        longPressedCallback?.invoke(currentItem)
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.d("AlarmItemGesture", "LongPress $currentItem")
        singleTapCallback?.invoke(currentItem)
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean = false

    override fun onShowPress(e: MotionEvent?) = Unit

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = false

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false
}