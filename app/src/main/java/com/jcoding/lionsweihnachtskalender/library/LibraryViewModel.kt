package com.jcoding.lionsweihnachtskalender.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel(){

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        startLoading()
    }

    fun startLoading() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(3000)
            _isLoading.value = false
        }
    }
}