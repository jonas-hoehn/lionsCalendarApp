@file:OptIn(ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender.screens

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.compose.LIONSWeihnachtskalenderTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.jcoding.lionsweihnachtskalender.BottomBarScreen
import com.jcoding.lionsweihnachtskalender.CustomItem
import com.jcoding.lionsweihnachtskalender.MainScreen
import com.jcoding.lionsweihnachtskalender.R
import com.jcoding.lionsweihnachtskalender.data.CalendarData
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    Column (modifier) {
        Spacer(Modifier.height(16.dp))
        LogoBar(Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(16.dp))
        GreetingsSection(paddingValues = PaddingValues())
        Spacer(Modifier.height(16.dp))
        HomeSection(title = R.string.scan_input){
            // Implement Camera handling
        }
    }

}




@Composable
fun HomeSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Implement composable here
    Column (modifier) {
        Text(
            stringResource(title).uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 16.dp)
                .padding(horizontal = 16.dp)
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoBar(
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
        Text(
            stringResource(id = R.string.app_name),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
    },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Image(
                    modifier = Modifier.size(50.dp, 50.dp),
                    contentDescription = "Lions Logo",
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = R.drawable.lcl_emblem_2color_web))
            }
        }
    )
}



// CAMERA STUFF


/*@Composable
fun MainContent(hasPermission: Boolean, onRequestPermission: () -> Unit) {

        if (hasPermission) {
            // If we have the permission, show the camera
            Text("Camera content")
            CameraScreen()
        } else {
            // If we do not have the permission, show the rationale
            Text("Camera permission required")
            NoPermissionScreen(onRequestPermission)
        }


}*/

/*@Composable
fun CameraScreen(){
    CameraContent()
}

@Composable
private fun CameraContent() {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    Scaffold (modifier = Modifier
        .fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                onClick = {
                    val mainExecutor = ContextCompat.getMainExecutor(context)
                    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            super.onCaptureSuccess(image)
                            image.toString()
                        }
                    })
                },
                icon = { Icon(Icons.Filled.DocumentScanner, "Localized description") },
                text = { Text(text = "Foto machen") },
            )
        }
    ){ paddingValues ->
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        setBackgroundColor(Color.BLACK)
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        previewView.controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            ){

            }
    }
}

@Composable
fun NoPermissionScreen(onRequestPermission: () -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Camera permission is required to continue")
        Button(
            onClick = onRequestPermission,
            Modifier.padding(top = 16.dp),
        ) {
            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera")
            Text(
                "Grant permission",
                Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun NoPermissionPrev() {
    NoPermissionScreen {

    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanTheContent(
    modifier: Modifier = Modifier,
) {
    Column (
        modifier = Modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var text by remember {
            mutableStateOf("")
        }

        var openCameraStateChange by remember {
            mutableStateOf(false)
        }

        val paddingValues = PaddingValues()
        val cameraPermissionState = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            //Handle permission result if needed
        }

        // Conditionally display MainContent based on state
        if (openCameraStateChange) {
            CameraScreen()
        }else{
           // NoPermissionScreen(onRequestPermission)
        }

        // possible other Composables

        val maxChar = 4
        var currentCharLength : Int = 0
        val context = LocalContext.current



        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = text,
            onValueChange = { newText ->
                if(newText.length <= maxChar){
                    currentCharLength = newText.length
                    text = newText
                }
            },
            label = {
                Text(text = "Kalendernummer")
            },
            placeholder = {
                Text(text = "4-stellige PIN")
            },
            singleLine = true,
            maxLines = 1,
            leadingIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Dialpad,
                        contentDescription = "Numbers Icon"
                    )
                }
            },
            trailingIcon = {
                if(text.length == maxChar){
                    IconButton(onClick = {
                        addCalendar(text = text, context = context)


                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Numbers Icon"
                        )
                    }
                }else{
                    IconButton(onClick = {
                        Log.d("Trailing Icon", "Clicked")
                        Toast.makeText(context, "Bitte vier Zahlen eingeben.", Toast.LENGTH_LONG).show()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Block,
                            contentDescription = "Numbers Icon"
                        )
                    }
                }

            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    addCalendar(text = text, context = context)
                    Log.d("ImeAction", "clicked")
                }
            )
        )

        //EXTENDED FAB
        ExtendedFloatingActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            onClick = { openCameraStateChange = true }, // Trigger state change
            icon = { Icon(Icons.Filled.DocumentScanner, "Localized description") },
            text = { Text(text = "Kamera öffnen") },
        )

    }
}

*/


private fun addCalendar(text: String, context: Context) {
    Log.d("Trailing Icon", "Clicked")
    //Toast.makeText(context, "Trailing Icon clicked", Toast.LENGTH_LONG).show()
    val calendarData = CalendarData(text.toInt(), true)
    if (CalendarRepository.contains(calendarData)) {
        Toast.makeText(context, "Kalender wurde bereits eingelöst.", Toast.LENGTH_LONG).show()
        return
    } else {
        Toast.makeText(context, "Kalender eingelöst", Toast.LENGTH_LONG).show()
        CalendarRepository.addDataEntry(calendarData)
    }
}


@Composable
fun GreetingsSection(paddingValues: PaddingValues) {
    val name = "User"
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingValues.calculateTopPadding()),
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start,
        ){
            Box (
            ){
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ){
                            append("Welcome, ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ){
                            append(name)
                        }
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}
