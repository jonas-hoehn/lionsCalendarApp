@file:OptIn(ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcoding.lionsweihnachtskalender.MainScreen
import com.jcoding.lionsweihnachtskalender.R
import com.jcoding.lionsweihnachtskalender.data.CalendarData
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    val name = "User"
    val scrollBehaviorTopBar = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val scrollBehaviorBottomNavigation = NavigationBarDefaults.windowInsets

    val listState = rememberLazyListState()
// The FAB is initially expanded. Once the first visible item is past the first item we
// collapse the FAB. We use a remembered derived state to minimize unnecessary compositions.
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }


    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehaviorTopBar.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    Text(
                        "Lions CLUB",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary
                    ))
                },
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Image(
                            modifier = Modifier.size(50.dp, 50.dp),
                            painter = painterResource(id = R.drawable.lcl_emblem_2color_web),
                            contentDescription = "Lions Logo",
                            contentScale = ContentScale.Fit
                        )
                    }
                },
                scrollBehavior = scrollBehaviorTopBar
            )
        },
       // containerColor = MaterialTheme.colorScheme.primaryContainer


    ) { innerPadding ->

        GreetingsSection(innerPadding)
        ManualNumber(innerPadding)
        EFABmain(innerPadding)


    }

}

@Composable
fun ManualNumber(paddingValues: PaddingValues) {

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(
                paddingValues.calculateBottomPadding()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var text by remember {
            mutableStateOf("")
        }



        val maxChar = 4
        var currentCharLength : Int = 0
        val context = LocalContext.current

        val TAG = "Error Toast"



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

    }
    

}

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
fun EFABmain(paddingValues: PaddingValues) {

    val mainScreen = MainScreen()

        ExtendedFloatingActionButton(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .padding(12.dp),
            onClick = { /* do something */ },
            icon = { Icon(Icons.Filled.DocumentScanner, "Localized description") },
            text = { Text(text = "Scan the number") },
        )

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
