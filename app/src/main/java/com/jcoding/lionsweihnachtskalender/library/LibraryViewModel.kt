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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.jcoding.lionsweihnachtskalender.data.CalendarData
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATABASE_SCANS = "calendar-scans/"

class LibraryViewModel : ViewModel(){


    val database = Firebase.database

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    var color by mutableStateOf(Color.Transparent)
        private set



    init {

        startLoading()
    }


    fun listenForScanUpdates(
        updateScanSnapshot: (DataSnapshot) -> Unit
    ) {
        val scansRef: DatabaseReference = database.getReference(DATABASE_SCANS)
        val query = scansRef.orderByChild("timestamp")

        // Use a CoroutineScope to handle asynchronousoperations
        CoroutineScope(Dispatchers.IO).launch {
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    updateScanSnapshot(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle potential errors here
                    //Timber.e(error.toException(), "Error listening for scan updates")
                }
            })
        }


    }

    fun startLoading() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(3000L)
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

    fun deleteCalendarItem(calendarItem: CalendarData, calendarNumber: Int) : Boolean{
        val calendarRepo = CalendarRepository
        var successful = false
        successful = calendarRepo.removeDataEntry(calendarItem, calendarNumber)
        return successful
    }


}