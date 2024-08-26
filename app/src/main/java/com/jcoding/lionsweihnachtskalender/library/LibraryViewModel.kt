package com.jcoding.lionsweihnachtskalender.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.jcoding.lionsweihnachtskalender.data.CalendarData
import com.jcoding.lionsweihnachtskalender.data.CalendarDataFirebase
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel(){


    val database = Firebase.database

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    var color by mutableStateOf(Color.Transparent)
        private set

    init {

        startLoading()
    }


    fun writeCalendarScan(number: Int, date: String, time: String, cashier: String){
        val myRef = database.getReference("calendar-scans/$number")
        val cDataFirebase = CalendarDataFirebase(number, date, time, cashier)
        myRef.setValue(cDataFirebase)
    }


    fun startLoading() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(3000)
            _isLoading.value = false
        }
    }

    @Composable
    @OptIn(ExperimentalMaterialApi::class)
    fun DeleteBackground(
        swipeDismissState: DismissState
    ){
        color = if(swipeDismissState.dismissDirection == DismissDirection.EndToStart){
            Color.Red
        } else Color.Transparent

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color)
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.White
            )
        }



    }

    fun deleteCalendarItem(calendarItem: CalendarData, index: Int) : Boolean{
        val calendarRepo = CalendarRepository
        var successful = false
        successful = calendarRepo.removeDataEntry(calendarItem, index = index)
        return successful
    }


}