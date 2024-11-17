package com.jcoding.lionsweihnachtskalender.camera

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.jcoding.lionsweihnachtskalender.signinsignup.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.jcoding.lionsweihnachtskalender.Destinations
import com.jcoding.lionsweihnachtskalender.data.CalendarData
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "CameraPreview"
val database = Firebase.database

@Composable
fun CameraPreviewForScanning(
    navController: NavHostController,
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    //Snackbar
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val scope = rememberCoroutineScope()


    val lifecycleOwner = LocalLifecycleOwner.current
    val context: Context = LocalContext.current
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }
    val detectedText: String by remember { mutableStateOf("Keine Nummer erkannt") }
    var shownText: String by remember { mutableStateOf("Keine Nummer erkannt") }

    MaterialTheme.colorScheme.surface
    var backgroundColor: Color by remember { mutableStateOf(Color(255,255,255)) }

    fun onTextUpdated(updatedText: String) {

        if (updatedText != shownText) {
            Log.d("DetectedText", "ShownText: $updatedText")
            shownText = updatedText // Add '#' if valid
            backgroundColor = Color(0xFF90EE90)
        } else {
            Log.d("DetectedText", "ShownText hasn't changed: $updatedText")
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        // Starts text recognition and provides the camera
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

        ClickableText(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 200.dp)
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .wrapContentSize(Alignment.Center)
                .padding(16.dp),
            text = AnnotatedString(
                shownText
            ),
            style = MaterialTheme.typography.headlineSmall.copy(),
            onClick = {
                val calendarNumber = shownText.drop(1)
                try {
                    val number = Integer.parseInt(calendarNumber)
                    if (CalendarRepository.containsNumber(number)) {
                        val cd = CalendarRepository.getCalendarDataByNumber(number)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Kein Rabatt mehr möglich. Die Nummer ${cd.number } wurde am ${cd.date} um ${cd.time} schon verwendet (KassiererIn: ${cd.cashier}). Wenden Sie sich bei Fragen an die Kassenaufsicht.",
                                actionLabel = "Okay!",
                                duration =  SnackbarDuration.Indefinite
                            )
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Erfolgreiche Erfassung. Die Nummer ${number} wurde soeben für einen Rabatt eingelöst.",
                                actionLabel = "Okay!",
                                duration =  SnackbarDuration.Indefinite
                            )
                        }
                        writeCalendarScan(
                            number,
                            UserRepository.getManagedUser().displayName.toString()
                        )
                        Toast.makeText(
                            context,
                            "Der Kalender wurde erfasst. Bitte jetzt über die Kasse scannen.",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate(Destinations.REPORT_ROUTE)
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        context,
                        "$e $detectedText ist KEINE valide Kalendernummer",
                        Toast.LENGTH_LONG
                    ).show()
                }


/*                if (isValidText) {
                    // remove first character
                    detectedText = detectedText.drop(1)

                    // FIXME an den anderen Stellen, wo die Nummer eingelöst werden kann,
                    // wenn möglich nicht den Code duplizieren, sondern den Code in eine Funktion auslagern
                    try {
                        val number = Integer.parseInt(detectedText)
                        if (CalendarRepository.containsNumber(number)) {
                            val cd = CalendarRepository.getCalendarDataByNumber(number)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Kein Rabatt mehr möglich. Die Nummer ${cd.number } wurde am ${cd.date} um ${cd.time} schon verwendet (KassiererIn: ${cd.cashier}). Wenden Sie sich bei Fragen an die Kassenaufsicht.",
                                    actionLabel = "Okay!"
                                )
                                // FIXME: sollte erst zugehen, wenn ok gedrückt wird
                            }
                            scope.launch {

                                cameraViewModel.eventFlow.collectLatest {
                                    when(it){
                                        is CameraViewModel.UIEvent.ShowSnackbar -> {
                                            snackbarHostState.showSnackbar(
                                                message = it.message,
                                                actionLabel = "Okay!"
                                            )
                                }}
                            }}
                        } else
                        {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${number} verwendet",
                                    actionLabel = "Okay!"
                                )
                            }
                            writeCalendarScan(number, UserRepository.getManagedUser().displayName.toString())
                            Toast.makeText(context, "Der Kalender wurde erfasst. Bitte jetzt über die Kasse scannen.", Toast.LENGTH_LONG).show()
                            navController.navigate(Destinations.REPORT_ROUTE)
                        }

                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "$e $detectedText ist KEINE valide Kalendernummer", Toast.LENGTH_LONG).show()
                    }
                }*/
            },
            maxLines = 1
        )
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(bottom = 32.dp)
            .offset(y = 80.dp)
    )

}

@Preview
@Composable
private fun CameraPreviewForScanningPrev() {
    CameraPreviewForScanning(
        navController = NavHostController(LocalContext.current),
        controller = LifecycleCameraController(LocalContext.current)
    )
}


// adds the scanned calendar to the Firebase Database using userRef and it's credentials
// FIXME: Sollte eigentlich eine eigene Action sein (!)
fun writeCalendarScan(number: Int, cashier: String){
    val myRef = database.getReference("calendar-scans/$number")
    val now = Date()
    var formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val formattedDate = formatter.format(now)
    formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val formattedTime = formatter.format(now)
    val cDataFirebase = CalendarData(number, formattedDate, formattedTime, cashier, now.time)
    myRef.setValue(cDataFirebase)
}


// starts the text recognition process using the LifecycleCameraController and the provided onDetectedTextUpdated callback
//FIXME: Sollte eigentlich das # Problem in TextRecognitionAnalyzer lösen!
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
