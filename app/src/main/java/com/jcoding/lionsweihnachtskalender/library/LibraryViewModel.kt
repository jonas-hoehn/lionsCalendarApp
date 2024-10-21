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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.launch

private const val DATABASE_SCANS = "calendar-scans/"

class LibraryViewModel : ViewModel(){


    val database = Firebase.database

    private val _calendarDataList = MutableLiveData<List<CalendarData>>(emptyList())
    val calenderDataList: LiveData<List<CalendarData>> = _calendarDataList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    var color by mutableStateOf(Color.Transparent)
        private set


    init {
        /* viewModelScope.launch {
            _isLoading.value = true
            CalendarRepository.removeAllData()
            listenForScanUpdates { dataSnapshot ->
                val updatedList = mutableListOf<CalendarData>()
                dataSnapshot.children.forEach {
                    val calendarData = it.getValue(CalendarData::class.java)
                    calendarData?.number = it.key!!.toInt()
                    if (calendarData != null) {
                        updatedList.add(calendarData)
                        CalendarRepository.addDataEntry(calendarData)
                    }
                }
                _calendarDataList.value = updatedList // Update LiveData
                _isLoading.value = false
            }
        } */
    }


    fun listenForScanUpdates(
        updateScanSnapshot: (DataSnapshot) -> Unit
    ) {
        val scansRef: DatabaseReference = database.getReference(DATABASE_SCANS)
        val query = scansRef.orderByChild("timestamp")

        // Use a CoroutineScope to handle asynchronous operations
        CoroutineScope(Dispatchers.IO).launch {
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    updateScanSnapshot(snapshot)
                    _isLoading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle potential errors here
                    //Timber.e(error.toException(), "Error listening for scan updates")
                    _isLoading.value = false
                }
            })
        }


    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            CalendarRepository.removeAllData()
            listenForScanUpdates { dataSnapshot ->
                /*
                val updatedList = mutableListOf<CalendarData>()
                dataSnapshot.children.forEach {
                    val calendarData = it.getValue(CalendarData::class.java)
                    calendarData?.number = it.key!!.toInt()
                    if (calendarData != null) {
                        updatedList.add(calendarData)
                        CalendarRepository.addDataEntry(calendarData)
                    }
                }
                _calendarDataList.value = updatedList */
                _isLoading.value = false
            }
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