@file:OptIn(ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender

import SettingsScreen
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jcoding.lionsweihnachtskalender.signinsignup.SignInRoute
import com.jcoding.lionsweihnachtskalender.signinsignup.SignUpRoute
import com.jcoding.lionsweihnachtskalender.signinsignup.User
import com.jcoding.lionsweihnachtskalender.signinsignup.UserRepository
import com.jcoding.lionsweihnachtskalender.signinsignup.WelcomeRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jcoding.lionsweihnachtskalender.Destinations.DETAILED_PROFILE_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.HOME_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.MAINSCREEN_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.NO_ACC_PERM
import com.jcoding.lionsweihnachtskalender.Destinations.OVERVIEW_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.REPORT_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.SETTINGS_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.SIGN_IN_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.SIGN_UP_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.WELCOME_ROUTE
import com.jcoding.lionsweihnachtskalender.camera.MainScreen
import com.jcoding.lionsweihnachtskalender.library.LibraryScreen
import com.jcoding.lionsweihnachtskalender.no_permission.NoAccountPermissionScreen
import com.jcoding.lionsweihnachtskalender.overview.OverviewRoute
import com.jcoding.lionsweihnachtskalender.presentation.settings.components.DetailedProfileScreen
import com.jcoding.lionsweihnachtskalender.screens.HomeScreen

object Destinations {
    const val OVERVIEW_ROUTE = "overview"
    const val MAINSCREEN_ROUTE = "cameramanagement"
    const val REPORT_ROUTE = "repport"
    const val WELCOME_ROUTE = "welcome"
    const val HOME_ROUTE = "home"
    const val SIGN_UP_ROUTE = "signup/{email}"
    const val SIGN_IN_ROUTE = "signin/{email}"
    const val NO_ACC_PERM = "noAccountPermission"
    const val SETTINGS_ROUTE = "settings"
    const val DETAILED_PROFILE_ROUTE = "detailedProfileRoute"
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainApplicationNavHost(
    navController: NavHostController = rememberNavController()
) {



    NavHost(
        navController = navController,
        startDestination = WELCOME_ROUTE
    ) {



        composable(WELCOME_ROUTE) {
            WelcomeRoute(
                onNavigateToSignIn = {
                    navController.navigate("signin/$it")
                },
                onNavigateToSignUp = {
                    navController.navigate("signup/$it")
                },
                onSignInAsGuest = {
                    navController.navigate(NO_ACC_PERM) //Hier war voher MAINSCREEN_ROUTE
                },
            )
        }

        composable(OVERVIEW_ROUTE) {
            OverviewRoute(
                navController,
                onHomeClicked = {
                    navController.navigate(SETTINGS_ROUTE)
                },
                onReportClicked = {
                    navController.navigate(REPORT_ROUTE)
                },
                onLogoutClicked = {
                    navController.navigate(WELCOME_ROUTE)
                }
            )
        }

        composable(SIGN_IN_ROUTE) {
            val startingEmail = it.arguments?.getString("email")
            SignInRoute(
                email = startingEmail,
                onSignInSubmitted = {
                    if (UserRepository.getManagedUser() is User.LoggedInUser) {
                        navController.navigate(MAINSCREEN_ROUTE) {
                            popUpTo(MAINSCREEN_ROUTE) {
                                inclusive = true
                            }
                        }
                    } else {
                        Toast.makeText(
                            navController.context,
                            "Login fehlgeschlagen",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate(SIGN_IN_ROUTE)
                    }
                },
                onSignInAsGuest = {
                    // FIXME
                    navController.navigate(NO_ACC_PERM)
                },
                onNavUp = navController::navigateUp,
            )
        }

        composable(SIGN_UP_ROUTE) {
            val startingEmail = it.arguments?.getString("email")
            SignUpRoute(
                email = startingEmail,
                onSignUpSubmitted = {
                    navController.navigate(MAINSCREEN_ROUTE)
                },
                onSignInAsGuest = {
                    navController.navigate(MAINSCREEN_ROUTE)
                },
                onNavUp = navController::navigateUp,
            )
        }



        composable(MAINSCREEN_ROUTE) {
            MainScreen(
                navController,
                modifier = Modifier.fillMaxSize(),
                onReportClicked = {
                    navController.navigate(REPORT_ROUTE)
                },
                onHomeClicked = {
                    navController.navigate(SETTINGS_ROUTE)
                },
                onLogoutClicked = {
                    navController.navigate(OVERVIEW_ROUTE)
                },
                onNavUp = navController::navigateUp
            )
        }

        composable(SETTINGS_ROUTE){
            SettingsScreen(
                navController
            )
        }

        composable(DETAILED_PROFILE_ROUTE){
            DetailedProfileScreen(
                navController
            )
        }

        composable(HOME_ROUTE){
            HomeScreen(
                modifier = Modifier,
                navController
            )
        }

        composable(REPORT_ROUTE) {
            LibraryScreen(
                navController,
                onReportClicked = {
                    navController.navigate(REPORT_ROUTE)
                }
            )
        }
        composable(NO_ACC_PERM){
            NoAccountPermissionScreen(
                onNavUp = navController::navigateUp
            )
        }

    }
}

