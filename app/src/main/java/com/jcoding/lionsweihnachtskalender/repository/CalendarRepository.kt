package com.jcoding.lionsweihnachtskalender.repository

import android.util.Log
import com.jcoding.lionsweihnachtskalender.data.CalendarData

const val TAG = "CalendarRepository"

object CalendarRepository {

    private val calenderEntryList: MutableList<CalendarData> = mutableListOf()


    fun getAllData(): List<CalendarData> {
        return calenderEntryList
    }

    fun removeAllData() {
        calenderEntryList.clear()
    }

    fun addDataEntry(calendarData: CalendarData) {
        calenderEntryList.add(calendarData)
    }

    fun removeDataEntry(calendarData: CalendarData, calendarNumber: Int): Boolean {

        val index = calenderEntryList.indexOf(calendarData)
        if (index in calenderEntryList.indices) {
            if (index >= 0 && index < calenderEntryList.size) {
                calenderEntryList.removeAt(index)
                return true
            } else return false
        } else {
            Log.e(TAG, "Invalid index: $index")
            return false
        }
    }

    fun containsNumber(number: Int): Boolean {
        for (calendarData in calenderEntryList) {
            if (calendarData.number == number) {
                return true
            }
        }
        return false
    }
}