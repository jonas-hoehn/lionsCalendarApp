package com.jcoding.lionsweihnachtskalender.camera

import android.content.Context
import android.widget.Toast
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.jcoding.lionsweihnachtskalender.Destinations
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
import com.jcoding.lionsweihnachtskalender.signinsignup.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class CameraViewModel: ViewModel(){

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    //private val _scanResult = MutableSharedFlow<>()

    fun showSnackbar(message: String){
        viewModelScope.launch {
            _eventFlow.emit(
                UIEvent.ShowSnackbar(message)
            )
        }
    }

    sealed class UIEvent{
        data class ShowSnackbar(val message: String): UIEvent()
    }

    fun handleCalendarScan(
        shownText: String,
        snackbarHostState: androidx.compose.material.SnackbarHostState,
        navController: NavHostController,
        context: Context
    ){

        viewModelScope.launch {
            val calendarNumber = shownText.drop(1)
            try {
                val number = Integer.parseInt(calendarNumber)
                if (CalendarRepository.containsNumber(number)) {
                    val cd = CalendarRepository.getCalendarDataByNumber(number)
                    viewModelScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Kein Rabatt mehr möglich. Die Nummer ${cd.number } wurde am ${cd.date} um ${cd.time} schon verwendet (KassiererIn: ${cd.cashier}). Wenden Sie sich bei Fragen an die Kassenaufsicht.",
                            actionLabel = "Okay!",
                            duration =  SnackbarDuration.Indefinite)
                    }
                } else {
                    viewModelScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Erfolgreiche Erfassung. Die Nummer ${number} wurde soeben für einen Rabatt eingelöst.",
                            actionLabel = "Okay!",
                            duration =  SnackbarDuration.Indefinite)
                    }
                    writeCalendarScan(
                        number,
                        UserRepository.getManagedUser().displayName.toString()
                    )
                    Toast.makeText(
                        context,
                        "Der Kalender wurde erfasst. Bitte jetzt über die Kasse scannen.",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(Destinations.REPORT_ROUTE)
                }
            }catch (e: NumberFormatException){
                snackbarHostState.showSnackbar(
                    message = "$e $shownText ist KEINE valide Kalendernummer",
                    actionLabel = "Okay!"
                )
            }
        }
    }

}