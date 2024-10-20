package com.jcoding.lionsweihnachtskalender

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

private const val DATABASE_SCANS = "calendar-scans/"

class MainViewModel: ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()
    val database = Firebase.database

    init {
        viewModelScope.launch {
            // FIXME delay?
            delay(3000) // Place where you check if user is logged in
            _isReady.value = true

            listenForScanUpdates { dataSnapshot ->
                CalendarRepository.removeAllData()
                dataSnapshot.children.forEach {
                    val calendarData = it.getValue(CalendarData::class.java)
                    calendarData?.number = it.key!!.toInt()
                    if (calendarData != null) {
                        CalendarRepository.addDataEntry(calendarData)
                    }
                }
                CalendarRepository.refresh()
            }
        }
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
                    //_isLoading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle potential errors here
                    //Timber.e(error.toException(), "Error listening for scan updates")
                    //_isLoading.value = false
                }
            })
        }
    }
}