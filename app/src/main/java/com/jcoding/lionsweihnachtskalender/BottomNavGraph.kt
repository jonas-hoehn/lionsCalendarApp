package com.jcoding.lionsweihnachtskalender

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jcoding.lionsweihnachtskalender.screens.HomeScreen
import com.jcoding.lionsweihnachtskalender.screens.LibraryScreen
import com.jcoding.lionsweihnachtskalender.screens.SettingsScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ){
        composable(route = BottomBarScreen.Home.route){
            HomeScreen()
        }

        composable(route = BottomBarScreen.Library.route){
            LibraryScreen()
        }

        composable(route = BottomBarScreen.Settings.route){
            SettingsScreen()
        }
    }
}