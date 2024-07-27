@file:OptIn(ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jcoding.lionsweihnachtskalender.Destinations.MAINSCREEN_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.REPORT_ROUTE
import com.jcoding.lionsweihnachtskalender.Destinations.OVERVIEW_ROUTE
import com.jcoding.lionsweihnachtskalender.camera.CameraManagement
import com.jcoding.lionsweihnachtskalender.overview.OverviewRoute
import com.jcoding.lionsweihnachtskalender.screens.LibraryScreen

object Destinations {
    const val OVERVIEW_ROUTE = "overview"
    const val MAINSCREEN_ROUTE = "cameramanagement"
    const val LOGOUT_ROUTE = "logout"
    const val REPORT_ROUTE = "repport"
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainApplicationNavHost(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = MAINSCREEN_ROUTE
    ){

        composable(MAINSCREEN_ROUTE){
            CameraManagement(
                modifier = Modifier.fillMaxSize(),


                )
        }

        composable(REPORT_ROUTE){
            LibraryScreen(
                onReportClicked = {
                    navController.navigate(Destinations.REPORT_ROUTE)
                }
            )
        }

        composable(OVERVIEW_ROUTE){
            OverviewRoute(
                onReportClicked = {
                    navController.navigate(Destinations.REPORT_ROUTE)
                },
                onLogoutClicked = {
                    navController.navigate(Destinations.LOGOUT_ROUTE)
                },
                onOpenCameraClicked = {
                    navController.navigate(MAINSCREEN_ROUTE)
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

