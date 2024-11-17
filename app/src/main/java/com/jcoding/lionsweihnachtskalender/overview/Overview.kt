package com.jcoding.lionsweihnachtskalender.overview

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.compose.LIONSWeihnachtskalenderTheme
import com.jcoding.lionsweihnachtskalender.signinsignup.User
import com.jcoding.lionsweihnachtskalender.signinsignup.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.jcoding.lionsweihnachtskalender.Destinations
import com.jcoding.lionsweihnachtskalender.R
import com.jcoding.lionsweihnachtskalender.camera.CameraViewModel
import com.jcoding.lionsweihnachtskalender.library.OrGoToReport
import com.jcoding.lionsweihnachtskalender.camera.writeCalendarScan

lateinit var auth: FirebaseAuth

@Composable
fun OverviewScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    onReportClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onHomeClicked: () -> Unit,
    onNavUp: () -> Unit
) {


    //Variable declarations here
    val showBranding by rememberSaveable { mutableStateOf(true) }

    // Implement composable here

    Scaffold(
        modifier
    ) { innerpadding ->

        //Column
        Column(
            modifier = Modifier
                .padding(innerpadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            AnimatedVisibility(
                showBranding,
                Modifier.fillMaxWidth()
            ) {
                Branding()
            }

            ManualInput(navHostController)

            OptionsMenu(
                onReportClicked =  onReportClicked,
                onHomeClicked = onHomeClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            OrLogoutFromApp(onLogoutClicked)
        }

    }
}

@Composable
fun ManualInput(
    navHostController: NavHostController
) {

    //Snackbar
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var text by remember {
        mutableStateOf("")
    }
    val maxChar = 4
    val context = LocalContext.current

    val cameraViewModel: CameraViewModel = CameraViewModel()
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        value = text,
        onValueChange = {newText ->
            if (newText.length <= maxChar) {
                text = newText
            }
        },
        placeholder = {
            Text(text = "Vierstellige Kalender-Nummer manuell erfassen (ohne Scannen)")
        },
        trailingIcon = {
            if (text.length == maxChar) {
                // hide keyboard display

                IconButton(onClick = {
                    focusManager.clearFocus()

                    //TODO Kalender hinzufügen
                    Log.d("TrailingIconOverview", "Calendar number is $text and clicked")
                    cameraViewModel.handleCalendarScan(
                        "#"+text,
                        snackbarHostState,
                        navHostController,
                        context
                    )
                }) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Numbers Icon"
                    )
                }
            } else {
                IconButton(onClick = {
                    Log.d("Trailing Icon", "Clicked")
                    focusManager.clearFocus()
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
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                cameraViewModel.handleCalendarScan(
                    "#"+text,
                    snackbarHostState,
                    navHostController,
                    context
                )
                Log.d("ImeAction", "clicked")
            }
        ),
    )

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .padding(bottom = 32.dp)
            .offset(y = 80.dp)
    )
}

@Composable
fun OptionsMenu(
    onReportClicked: () -> Unit,
    onHomeClicked: () -> Unit,
    modifier: Modifier = Modifier
) {

    //Layout of the options menu
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Composables of the options menu

        Button(
            onClick = onHomeClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 3.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(id = R.string.go_to_settings),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        OrGoToReport(
            onReportClicked = onReportClicked ,
        )
    }
}

@Composable
fun OrLogoutFromApp(
    onLogoutClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    auth = FirebaseAuth.getInstance()
    val onSubmit = {
        auth.signOut()
        onLogoutClicked()
    }

    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ){
        TextButton(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 24.dp),
        ) {
            Text(text = stringResource(id = R.string.logout_from_app))
        }
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {

    val user by UserRepository.managedUser.collectAsState()

    Column (
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ){
        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 76.dp)
        )
        Text(
            text = stringResource(id = R.string.app_tagline),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
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
    }
}

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    //Logo Implementation from resources
    val assetId = if (lightTheme){
        R.drawable.ic_logo_light_lions_foreground
    } else {
        R.drawable.ic_logo_light_lions_foreground
    }

    //Setting the logo in the composable
    Image(
        modifier = modifier,
        painter = painterResource(id = assetId),
        contentDescription = null
    )
}

@Preview(name = "Welcome light theme", uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Welcome dark theme", uiMode = UI_MODE_NIGHT_NO)
@Composable
private fun OverviewScreenPrev() {
    LIONSWeihnachtskalenderTheme {
        OverviewScreen(
            navHostController = NavHostController(LocalContext.current),
            onReportClicked = {},
            onLogoutClicked = {},
            onHomeClicked = {},
            onNavUp = {}
        )
    }

}