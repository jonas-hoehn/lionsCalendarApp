package com.jcoding.lionsweihnachtskalender.repository

import com.jcoding.lionsweihnachtskalender.data.CalendarData

object CalendarRepository {

    private val currentNumbersList: MutableList<CalendarData> = mutableListOf()


    fun getAllData() : List<CalendarData>{
        return currentNumbersList
    }

    fun addDataEntry(number: Int, scanned: Boolean){
        val calendarData = CalendarData(number, scanned)
        currentNumbersList.add(calendarData)
    }

    fun addDataEntry(calendarData: CalendarData){
        currentNumbersList.add(calendarData)
    }

    fun contains (calendarData: CalendarData): Boolean{
       return currentNumbersList.contains(calendarData)
    }
}