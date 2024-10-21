package com.jcoding.lionsweihnachtskalender.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jcoding.lionsweihnachtskalender.data.CalendarData

const val TAG = "CalendarRepository"

object CalendarRepository {

    private val _calendarDataList = MutableLiveData<List<CalendarData>>(emptyList())
    var calenderDataList: LiveData<List<CalendarData>> = _calendarDataList
    var updatedList = mutableListOf<CalendarData>()

    fun refresh() {
        _calendarDataList.value = updatedList
    }
    fun getAllData(): List<CalendarData> {
        return updatedList
    }

    fun removeAllData() {
        updatedList = mutableListOf<CalendarData>()
    }

    fun addDataEntry(calendarData: CalendarData) {
        updatedList.add(calendarData)
    }

    fun removeDataEntry(calendarData: CalendarData, calendarNumber: Int): Boolean {

        val index = updatedList.indexOf(calendarData)
        if (index in updatedList.indices) {
            if (index >= 0 && index < updatedList.size) {
                updatedList.removeAt(index)
                return true
            } else return false
        } else {
            Log.e(TAG, "Invalid index: $index")
            return false
        }
    }

    fun containsNumber(number: Int): Boolean {
        for (calendarData in updatedList) {
            if (calendarData.number == number) {
                return true
            }
        }
        return false
    }

    fun getCalendarDataByNumber(number: Int): CalendarData {
        for (calendarData in updatedList) {
            if (calendarData.number == number) {
                return calendarData
            }
        }
        return CalendarData()
    }
}