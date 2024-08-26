package com.jcoding.lionsweihnachtskalender.repository

import android.util.Log
import com.jcoding.lionsweihnachtskalender.data.CalendarData

const val TAG = "CalendarRepository"

object CalendarRepository {

    private val currentNumbersList: MutableList<CalendarData> = mutableListOf()


    fun getAllData() : List<CalendarData>{
        return currentNumbersList
    }

    fun addDataEntry(calendarData: CalendarData){
        currentNumbersList.add(calendarData)
    }

    fun removeDataEntry(calendarData: CalendarData, index: Int): Boolean{

        if(index in currentNumbersList.indices){
            if (index >= 0 && index < currentNumbersList.size){
                currentNumbersList.removeAt(index)
                return true
            } else return false
        }else{
            Log.e(TAG, "Invalid index: $index")
            return false
        }
    }

    fun contains (calendarData: CalendarData): Boolean{
       return currentNumbersList.contains(calendarData)
    }
}