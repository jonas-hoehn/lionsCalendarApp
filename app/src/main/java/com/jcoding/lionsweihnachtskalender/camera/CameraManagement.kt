package com.jcoding.lionsweihnachtskalender.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jcoding.lionsweihnachtskalender.CameraPreview
import com.jcoding.lionsweihnachtskalender.data.MainCameraViewModel
import com.jcoding.lionsweihnachtskalender.overview.OverviewScreen

@Composable
fun CameraManagement(
    navHostController: NavHostController,
    modifier: Modifier,
    onReportClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onHomeClicked: () -> Unit
) {

    CameraContent(
        navHostController,
        modifier,
        onReportClicked = onReportClicked,
        onLogoutClicked = onLogoutClicked,
        onHomeClicked = onHomeClicked
    )
}

private fun takePhoto(
    controller: LifecycleCameraController,
    applicationContext: Context,
    onPhotoTaken: (Bitmap) -> Unit
){
    controller.takePicture(
        ContextCompat.getMainExecutor(applicationContext),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }

                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldnt take Photo: ", exception)
            }
        }
    )
}

/*@Preview
@Composable
private fun CameraContentPrev() {
    CameraContent()
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraContent(
    navHostController: NavHostController,
    modifier : Modifier = Modifier,
    onHomeClicked: () -> Unit,
    onReportClicked: () -> Unit,
    onLogoutClicked: () -> Unit
){

    val scope = rememberCoroutineScope()


    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded,
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
                    onReportClicked = onReportClicked,
                    onLogoutClicked = onLogoutClicked,
                    onHomeClicked = onHomeClicked
                )
            }


        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ){
            Text(text = "Scaffold Content")

            CameraPreview(
                navHostController,
            controller = controller,
            modifier = Modifier
                .fillMaxSize()
            )

            IconButton(
                onClick = {},
                modifier = Modifier
                    .offset(16.dp, 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Switch Camera")
            }
        }
    }



/*    BottomSheetScaffold(
        sheetPeekHeight = 500.dp,
        scaffoldState = scaffoldState,
        sheetContent = {
            PhotoBottomSheetContent(
                bitmaps = bitmaps,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ){
            Text(text = "Scaffold Content")

*//*            CameraPreview(
                controller = controller,
                modifier = Modifier
                    .fillMaxSize()
            )

            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if(controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                },
                modifier = Modifier
                    .offset(16.dp, 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "Switch Camera")
            }*//*



            //LINE ON THE BOTTOM TO CONTROL THE CAMERA

*//*            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                
                IconButton(
                    onClick = {
                        //Open Gallery
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Photo, contentDescription = "Open Gallery")
                }
                IconButton(
                    onClick = {
                        // Separate function due to complexity -> handles the photo action
                        takePhoto(
                            controller = controller,
                            applicationContext = applicationContext,
                            onPhotoTaken = viewModel::onTakePhoto
                        )

                    }
                ) {
                    Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take Photo")
                }
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go to Homescreen")
                }
            }*//*
        }
    }*/
}