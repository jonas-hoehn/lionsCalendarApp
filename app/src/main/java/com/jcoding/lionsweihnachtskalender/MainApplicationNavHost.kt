@file:OptIn(ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.jetsurvey.signinsignup.SignInRoute
import com.example.compose.jetsurvey.signinsignup.SignUpRoute
import com.example.compose.jetsurvey.signinsignup.UserRepository
import com.example.compose.jetsurvey.signinsignup.WelcomeRoute
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jcoding.lionsweihnachtskalender.Destinations.MAINSCREEN_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.REPORT_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.OVERVIEW_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.SIGN_IN_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.SIGN_UP_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.WELCOME_ROUTE
import com.jcoding.lionsweihnachtskalender.camera.CameraManagement
import com.jcoding.lionsweihnachtskalender.overview.OverviewScreen
import com.jcoding.lionsweihnachtskalender.library.LibraryScreen
import com.jcoding.lionsweihnachtskalender.overview.OverviewRoute

object Destinations {
    const val OVERVIEW_ROUTE = "overview"
    const val MAINSCREEN_ROUTE = "cameramanagement"
    const val REPORT_ROUTE = "repport"
    const val WELCOME_ROUTE = "welcome"
    const val SIGN_UP_ROUTE = "signup/{email}"
    const val SIGN_IN_ROUTE = "signin/{email}"
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainApplicationNavHost(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = WELCOME_ROUTE
    ){

        composable(WELCOME_ROUTE) {
            WelcomeRoute(
                onNavigateToSignIn = {
                    navController.navigate("signin/$it")
                },
                onNavigateToSignUp = {
                    navController.navigate("signup/$it")
                },
                onSignInAsGuest = {
                    navController.navigate(OVERVIEW_ROUTE) //Hier war voher MAINSCREEN_ROUTE
                },
            )
        }

        composable(OVERVIEW_ROUTE){
            OverviewRoute(
                onHomeClicked = {
                    navController.navigate(MAINSCREEN_ROUTE)
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
                    navController.navigate(MAINSCREEN_ROUTE)
                },
                onSignInAsGuest = {
                    navController.navigate(MAINSCREEN_ROUTE)
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



        composable(MAINSCREEN_ROUTE){
            CameraManagement(
                navController,
                modifier = Modifier.fillMaxSize(),
                onReportClicked = {
                    navController.navigate(REPORT_ROUTE)
                },
                onHomeClicked = {
                    navController.navigate(OVERVIEW_ROUTE)
                },
                onLogoutClicked = {
                    navController.navigate(OVERVIEW_ROUTE)
                }
                )
        }
            composable(REPORT_ROUTE){
            LibraryScreen(
                navController,
                onReportClicked = {
                    navController.navigate(REPORT_ROUTE)
                }
            )
        }

    }




/*    MainContent(
        hasPermission = cameraPermissionState.hasPermission,
        onRequestPermission = cameraPermissionState::launchPermissionRequest,

    )*/



}

/*@Composable
private fun MainContent(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,

) {

    val showBottomBar = remember { mutableStateOf(true) }


    if (hasPermission) {
        val navController = rememberNavController()
        Scaffold (
            bottomBar = {
                if(showBottomBar.value){

                    BottomBar(
                        navController = navController,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                        }
        ){ innerPadding ->
            val context = LocalContext.current
            BottomNavGraph(navController = navController, innerPadding, showBottomBar)

        }
    } else {
       NoPermissionScreen(onRequestPermission)
    }
}


@Composable
fun BottomBar(navController: NavHostController, modifier: Modifier) {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Library,
        BottomBarScreen.Settings,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    NavigationBar (
        containerColor = NavigationBarDefaults.containerColor,
        contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor),
    ){
        screens.forEach { screen ->
            AddItem(screen = screen, currentDestination = currentDestination, navController = navController)
        }

    }

}


@Composable
fun RowScope.AddItem (
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
){
    NavigationBarItem(
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
            unselectedTextColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        ),
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon"
            )
        },
        label = {
            Text(text = screen.title)
        },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {
            navController.navigate(screen.route)
        }
    )
}*/

