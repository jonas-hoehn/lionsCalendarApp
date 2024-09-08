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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.jcoding.lionsweihnachtskalender.camera.TextRecognitionAnalyzer
import com.jcoding.lionsweihnachtskalender.data.CalendarData
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
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
    var detectedText: String by remember { mutableStateOf("Keine Nummer erkannt") }
    var validTextInfo: String = "❌"
    var isValidText : Boolean by remember { mutableStateOf( false) }
    var shownText: String by remember { mutableStateOf("Keine Nummer erkannt") }

    MaterialTheme.colorScheme.surface
    var backgroundColor: Color by remember { mutableStateOf(Color(255,255,255,)) }

    fun onTextUpdated(updatedText: String) {


        detectedText = updatedText.replace("[^#0-9]*".toRegex(), "")
        isValidText =  ("#[0-9]{4}".toRegex().matches(detectedText))
        validTextInfo=  if (isValidText) { "✅"} else { "❌" }
        backgroundColor = if (isValidText) { Color(0xFF90EE90) } else { Color(255,255,255,)}
        shownText = detectedText + " " + validTextInfo

        Log.d(TAG, "onTextUpdated: $detectedText $isValidText $validTextInfo")
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
                .padding(16. dp),
            text = AnnotatedString(
                shownText
            ),
            style = MaterialTheme.typography.headlineSmall.copy(),
            onClick = {
                if (isValidText) {
                    // remove first character
                    detectedText = detectedText.drop(1)

                    // FIXME an den anderen Stellen, wo die Nummer eingelöst werden kann,
                    // wenn möglich nicht den Code duplizieren, sondern den Code in eine Funktion auslagern
                    try {
                        val number = Integer.parseInt(detectedText)
                        if (CalendarRepository.containsNumber(number)) {
                            // Fixme -> Ein Toast ist nicht sinnvoll
                            //  Snackbar?
                            //  AlertDialog?
                            val cd = CalendarRepository.getCalendarDataByNumber(number)
                            Toast.makeText(context, "Kein Rabatt mehr möglich. Die Nummer ${cd.number } wurde am ${cd.date} um ${cd.time} schon verwendet (KassiererIn: $cd.cashier). Wenden Sie sich bei Fragen an die Kassenaufsicht.", Toast.LENGTH_LONG).show()
                        } else
                        {
                            // Fixme -> auch hier ist der Toast nicht angebracht
                            //  Snackbar?
                            //  AlertDialog?
                            writeCalendarScan(number, UserRepository.getManagedUser().displayName.toString())
                            Toast.makeText(context, "Der Kalender wurde erfasst. Bitte jetzt über die Kasse scannen.", Toast.LENGTH_LONG).show()
                            navController.navigate(Destinations.REPORT_ROUTE)
                        }

                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "$detectedText ist KEINE valide Kalendernummer", Toast.LENGTH_LONG).show()
                    }
                }
            },
            maxLines = 1
        )
    }

}

@Preview
@Composable
private fun CameraPreviewForScanningPrev() {
    CameraPreviewForScanning(
        navController = NavHostController(LocalContext.current),
        controller = LifecycleCameraController(LocalContext.current)
    )
}

fun writeCalendarScan(number: Int, cashier: String){
    val myRef = database.getReference("calendar-scans/$number")
    val now: Date = Date()
    var formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val formattedDate = formatter.format(now)
    formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val formattedTime = formatter.format(now)
    val cDataFirebase = CalendarData(number, formattedDate, formattedTime, cashier, now.time)
    myRef.setValue(cDataFirebase)
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
