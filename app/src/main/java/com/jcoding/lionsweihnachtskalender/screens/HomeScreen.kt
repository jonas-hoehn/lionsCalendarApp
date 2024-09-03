@file:OptIn(ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.NavHostController
import com.jcoding.lionsweihnachtskalender.signinsignup.UserRepository
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jcoding.lionsweihnachtskalender.R
import com.jcoding.lionsweihnachtskalender.camera.MainScreen
import com.jcoding.lionsweihnachtskalender.writeCalendarScan
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    var openCameraStateChange by remember {
        mutableStateOf(false)
    }


    Column(modifier) {
        Spacer(Modifier.height(16.dp))
        LogoBar(Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(16.dp))
        GreetingsSection(paddingValues = PaddingValues())
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            HomeSection(title = R.string.manual_input) {
                InputHandling(openCameraStateChange) { newState ->
                    openCameraStateChange = newState
                }

            }
        }

    }

    // Conditionally display MainContent based on state
    if (openCameraStateChange) {
        MainScreen(
            navHostController = NavHostController(LocalContext.current),
            modifier.fillMaxSize(),
            onHomeClicked = {},
            onLogoutClicked = {},
            onReportClicked = {},
        )
    }
}

@Composable
fun HomeSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Implement composable here
    Column(modifier) {
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
                    painter = painterResource(id = R.drawable.lcl_emblem_2color_web)
                )
            }
        }
    )
}


// CAMERA STUFF
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InputHandling(
    openCameraStateChange: Boolean,
    onCameraStateChanged: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        var text by remember {
            mutableStateOf("")
        }


        val paddingValues = PaddingValues()
        val cameraPermissionState = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            //Handle permission result if needed
        }


        // possible other Composables

        val maxChar = 4
        var currentCharLength = 0
        val context = LocalContext.current



        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = text,
            onValueChange = { newText ->
                if (newText.length <= maxChar) {
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
                        writeCalendarScan(
                            Integer.parseInt(text),
                            UserRepository.getManagedUser().displayName.toString()
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
                    writeCalendarScan(
                        Integer.parseInt(text),
                        UserRepository.getManagedUser().displayName.toString()
                    )
                    Log.d("ImeAction", "clicked")
                }
            )
        )


    }
}

@Preview
@Composable
private fun InputHandPrev() {
    InputHandling(openCameraStateChange = true) {

    }
}


@Composable
fun GreetingsSection(paddingValues: PaddingValues) {
    val name = "User"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingValues.calculateTopPadding()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start,
        ) {
            Box(
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("Welcome, ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            append(name)
                        }
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomePrev() {
    HomeScreen()
}