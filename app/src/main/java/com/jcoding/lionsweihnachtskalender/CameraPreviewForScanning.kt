package com.jcoding.lionsweihnachtskalender

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.jcoding.lionsweihnachtskalender.camera.TextRecognitionAnalyzer
import com.jcoding.lionsweihnachtskalender.library.LibraryViewModel
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
import com.jcoding.lionsweihnachtskalender.screens.AddCalendar

private const val TAG = "CameraPreview"

@Composable
fun CameraPreviewForScanning(
    navController: NavHostController,
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val maxChar = 4
    var currentCharLength: Int = 0
    val lifecycleOwner = LocalLifecycleOwner.current
    val context: Context = LocalContext.current
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }
    var detectedText: String by remember { mutableStateOf("No Number detected yet..") }

    fun onTextUpdated(updatedText: String) {

        val filteredString = updatedText.replace("[^0-9]".toRegex(), "")

        Log.d(TAG, "onTextUpdated: $filteredString")
        var currentNumber: Int = 0
        if (filteredString.isNotEmpty() && filteredString.length <= 4) {
            currentNumber = filteredString.toInt()

        }
        if (currentNumber != 0) {
            detectedText = currentNumber.toString()
            if (detectedText != "") {
                val error = AddCalendar(detectedText, context = context)
                AddCalendar(detectedText, context)
                val viewModel = LibraryViewModel()
                viewModel.writeCalendarScan(Integer.parseInt(detectedText), "Stefan")
                if (!error) {
                    navController.navigate(Destinations.REPORT_ROUTE)
                } else Unit
            }
        }


    }




    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        AndroidView(
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    controller.bindToLifecycle(lifecycleOwner)
                }.also { previewView ->
                    startTextRecognition(
                        context = it,
                        cameraController = cameraController,
                        lifecycleOwner = lifecycleOwner,
                        previewView = previewView,
                        onDetectedTextUpdated = ::onTextUpdated
                    )
                }
            },
            modifier = modifier
        )


        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 200.dp)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .wrapContentSize(Alignment.Center)
                .padding(16.dp),
            text = detectedText,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )


        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.Center)
                .offset(y = 300.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            value = text,
            onValueChange = { newText ->
                if (newText.length <= maxChar) {
                    if (CalendarRepository.containsNumber(newText.toInt())) {
                        Toast.makeText(context, "Die Zahl $newText ist schon vergeben.", Toast.LENGTH_LONG)
                            .show()
                        return@OutlinedTextField
                    }
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
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Dialpad,
                        contentDescription = "Numbers Icon"
                    )
                }
            },
            trailingIcon = {
                if (text.length == maxChar) {
                    IconButton(onClick = {
                        AddCalendar(text, context = context)
                        navController.navigate(Destinations.REPORT_ROUTE)
                        keyboardController?.hide()
                        text = ""

                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Numbers Icon"
                        )
                    }
                } else {
                    IconButton(onClick = {
                        Log.d("Trailing Icon", "Clicked")
                        Toast.makeText(context, "Bitte vier Zahlen eingeben.", Toast.LENGTH_LONG)
                            .show()
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
                    AddCalendar(text, context = context)
                    keyboardController?.hide()
                    text = "" //FIXME hier könnte die Nullpointer Exception fliegen
                    Log.d("ImeAction", "clicked")
                }
            ),
        )
    }

}


private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextUpdated: (String) -> Unit
) {
    cameraController.imageAnalysisTargetSize = CameraController.OutputSize(AspectRatio.RATIO_16_9)
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        TextRecognitionAnalyzer(onDetectedTextUpdated = onDetectedTextUpdated)
    )
    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}
