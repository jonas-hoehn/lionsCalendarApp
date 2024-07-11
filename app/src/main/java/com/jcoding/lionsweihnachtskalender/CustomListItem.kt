package com.jcoding.lionsweihnachtskalender

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcoding.lionsweihnachtskalender.data.CalendarData

@Composable
fun CustomItem(calendarData: CalendarData) {
    Row(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "${calendarData.number}",
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = calendarData.scanned.toString(),
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
@Preview
fun PreviewCustomItem(){
    CustomItem(calendarData = CalendarData(
        number = 1234,
        scanned = true
    ))
}