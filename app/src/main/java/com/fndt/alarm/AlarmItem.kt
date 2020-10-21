package com.fndt.alarm

data class AlarmItem(
    var time: Long,
    var name: String,
    var repeatPeriod: Array<Int>,
    var isActive: Boolean
) {
    companion object {
        fun Long.toTimeString() = "${this / 60}:${this % 60}"
    }
}

