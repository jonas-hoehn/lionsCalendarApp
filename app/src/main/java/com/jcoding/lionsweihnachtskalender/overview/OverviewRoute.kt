package com.jcoding.lionsweihnachtskalender.overview

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun OverviewRoute(
    navHostController: NavHostController,
    onHomeClicked: () -> Unit,
    onReportClicked: () -> Unit,
    onLogoutClicked: () -> Unit
) {
    OverviewScreen(
        navHostController,
        onReportClicked = onReportClicked,
        onLogoutClicked = onLogoutClicked,
        onHomeClicked = onHomeClicked,
    )
}