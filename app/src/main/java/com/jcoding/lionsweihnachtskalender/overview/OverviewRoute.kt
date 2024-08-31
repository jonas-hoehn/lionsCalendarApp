package com.jcoding.lionsweihnachtskalender.overview

import androidx.compose.runtime.Composable

@Composable
fun OverviewRoute(
    onHomeClicked: () -> Unit,
    onReportClicked: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    OverviewScreen(
        onReportClicked = onReportClicked,
        onLogoutClicked = onLogoutClicked,
        onHomeClicked = onHomeClicked,
    )
}