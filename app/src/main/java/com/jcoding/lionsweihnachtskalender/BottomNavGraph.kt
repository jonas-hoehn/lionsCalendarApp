package com.jcoding.lionsweihnachtskalender

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jcoding.lionsweihnachtskalender.screens.HomeScreen
import com.jcoding.lionsweihnachtskalender.screens.LibraryScreen
import com.jcoding.lionsweihnachtskalender.screens.SettingsScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    showBottomBar: MutableState<Boolean>,

    ) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ){
        composable(route = BottomBarScreen.Home.route){
            showBottomBar.value = true
            HomeScreen()
        }

        composable(route = BottomBarScreen.Library.route){
            showBottomBar.value = true
            LibraryScreen(navController,
                List<String>(1000){"$it"}
            ){
                navController.navigate(Destinations.REPORT_ROUTE)
            }
        }

        composable(route = BottomBarScreen.Settings.route){
            showBottomBar.value = false
            SettingsScreen()
        }
    }
}