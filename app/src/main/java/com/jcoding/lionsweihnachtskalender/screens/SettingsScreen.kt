package com.jcoding.lionsweihnachtskalender.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcoding.lionsweihnachtskalender.camera.CameraManagement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CameraManagement(modifier = Modifier)
        }
    }


}

@Preview
@Composable
private fun SettingsScreenPrev() {
    SettingsScreen()
}