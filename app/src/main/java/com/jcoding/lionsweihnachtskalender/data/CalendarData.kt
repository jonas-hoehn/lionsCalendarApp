package com.jcoding.lionsweihnachtskalender.data

data class CalendarData(
    var number: Int = 0,
    var date: String = "",
    var time: String = "",
    var cashier: String = "",
    var timestamp: Long = 0,

    var scanned: Boolean = false
)
