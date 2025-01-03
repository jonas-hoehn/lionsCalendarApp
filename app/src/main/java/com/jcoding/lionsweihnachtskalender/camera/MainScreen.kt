package com.jcoding.lionsweihnachtskalender.camera

import android.content.Context
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jcoding.lionsweihnachtskalender.signinsignup.UserRepository
import com.jcoding.lionsweihnachtskalender.Destinations
import com.jcoding.lionsweihnachtskalender.data.MainCameraViewModel
import com.jcoding.lionsweihnachtskalender.overview.OverviewScreen
import com.jcoding.lionsweihnachtskalender.signinsignup.User

@Composable
fun MainScreen(
    navHostController: NavHostController,
    modifier: Modifier,
    onReportClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onHomeClicked: () -> Unit,
    onNavUp: () -> Unit
) {

    MainScreenContent(
        navHostController,
        modifier,
        onReportClicked = onReportClicked,
        onLogoutClicked = onLogoutClicked,
        onHomeClicked = onHomeClicked,
        onNavUp = onNavUp
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    navHostController: NavHostController,
    modifier : Modifier = Modifier,
    onHomeClicked: () -> Unit,
    onReportClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onNavUp: () -> Unit,
){

    val scope = rememberCoroutineScope()
    val user by UserRepository.managedUser.collectAsState()

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    val applicationContext: Context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(applicationContext).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or
                        CameraController.VIDEO_CAPTURE
            )
        }
    }

    val viewModel = viewModel<MainCameraViewModel>()
    val bitmaps by viewModel.bitmaps.collectAsState()

    BottomSheetScaffold(
        modifier = Modifier,
        scaffoldState = scaffoldState,
        sheetPeekHeight = 100.dp,
        sheetContent = {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(700.dp),
            ){
                OverviewScreen(
                    modifier = Modifier,
                    navHostController = navHostController,
                    onReportClicked = onReportClicked,
                    onLogoutClicked = onLogoutClicked,
                    onHomeClicked = onHomeClicked,
                    onNavUp = onNavUp
                )
            }


        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ){
            CameraPreviewForScanning(
                navHostController,
            controller = controller,
            modifier = Modifier
                .fillMaxSize()
            )
            if (user is User.LoggedInUser) {
                Text(
                    text = "${user.displayName} (${user.role})",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                )
            }
            IconButton(
                onClick = {
                    UserRepository.signOut()
                    navHostController.navigate(Destinations.WELCOME_ROUTE)
                },
                modifier = Modifier
                    .offset(16.dp, 32.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
            }
        }
    }


}