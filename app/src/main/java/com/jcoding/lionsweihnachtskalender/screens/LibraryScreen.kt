@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender.screens

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.LIONSWeihnachtskalenderTheme
import com.example.compose.stronglyDeemphasizedAlpha
import com.jcoding.lionsweihnachtskalender.CustomItem
import com.jcoding.lionsweihnachtskalender.Destinations
import com.jcoding.lionsweihnachtskalender.R
import com.jcoding.lionsweihnachtskalender.data.CalendarData
import com.jcoding.lionsweihnachtskalender.effects.AnimatedShimmer
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavHostController,
    onReportClicked: () -> Unit
) {


    var showOnboarding by remember { mutableStateOf(true) }
    var listSize by remember { mutableStateOf(0) }

    val getAllData = CalendarRepository.getAllData()
    listSize = getAllData.size

    if (showOnboarding) {
        OnboardingScreen(navController, updateShowOnboarding = {
            showOnboarding = it
        })
    } else {
        HandleList(
            navController,
            onReportClicked,
            listSize,
            calendarData = getAllData,
            showOnboarding = showOnboarding,
            updateShowOnboarding = { newValue ->
                showOnboarding = newValue
            }
        )
    }


}

@Composable
fun OnboardingScreen(navController: NavHostController, updateShowOnboarding: (Boolean) -> Unit) {

    val context: Context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Destinations.MAINSCREEN_ROUTE)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Icon"
                        )
                    }
                },
                title = {
                    Text(text = "Kalenderbericht")
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(bottom = 16.dp),
                onClick = {
                    /*Toast.makeText(context, "Feature noch nicht verfügbar", Toast.LENGTH_SHORT)
                        .show()*/
                    AddCalendar("1234", context)
                    updateShowOnboarding(false)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(text = "Neuer Eintrag")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Keine Einträge vorhanden")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandleList(
    navController: NavHostController,
    onReportClicked: () -> Unit,
    listSize: Int,
    calendarData: List<CalendarData>,
    showOnboarding: Boolean,
    updateShowOnboarding: (Boolean) -> Unit,
) {

    val context: Context = LocalContext.current
    val getAllData = calendarData
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showShimmer by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        delay(1000)
        showShimmer = false
        updateShowOnboarding(getAllData.isEmpty())
    }


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(Destinations.MAINSCREEN_ROUTE)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Icon"
                        )
                    }
                },
                title = {
                    Text(text = "Kalenderbericht")
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(bottom = 16.dp),
                onClick = {
                    /*Toast.makeText(context, "Feature noch nicht verfügbar", Toast.LENGTH_SHORT)
                        .show()*/
                    AddCalendar("1234", context)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(text = "Neuer Eintrag")
            }
        }
    ) { innerPadding ->


        Column {
            if (showShimmer) {
                repeat(10) {
                    AnimatedShimmer()
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = innerPadding.calculateTopPadding())
                            .padding(vertical = 4.dp)
                    ) {
                        LazyColumn {

                            itemsIndexed(
                                items = getAllData,
                                key = { index, calendarData ->
                                    calendarData.number
                                }
                            )
                            { index, calendarItem ->
                                Log.d("Index of LazyList: ", index.toString())
                                CalendarItem(calendarData = calendarItem)

                            }
                        }
                    }
                }

            }

        }

    }

}

@Composable
fun CalendarItem(calendarData: CalendarData) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    val extraPadding by animateDpAsState(
        targetValue = if (expanded) 48.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 1000
        )
    )

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = extraPadding.coerceAtLeast(0.dp)),
            ) {
                Row (
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ){

                    Text(
                        text = "${calendarData.number}",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (calendarData.scanned) "Eingelöst" else "Nicht eingelöst",
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                }

                if(expanded){
                    Text(text = "Custom Item")
                }
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    }
                )
            }
        }
    }
}


@Preview
@Composable
private fun LibraryScreenPrev() {
    LIONSWeihnachtskalenderTheme {
        val navController: NavHostController = rememberNavController()
        LibraryScreen(navController, onReportClicked = {
            navController.navigate(Destinations.REPORT_ROUTE)
        })
    }
}

@Composable
fun CustomList(calendarRepository: CalendarRepository, paddingValues: PaddingValues) {


    val getAllData = calendarRepository.getAllData()

    LazyColumn(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .padding(bottom = paddingValues.calculateBottomPadding())
            .padding(start = 12.dp, end = 12.dp, top = 12.dp)
            .clip(RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(
            items = getAllData,
            key = { index, calendarData ->
                calendarData.number
            }
        )
        { index, calendarItem ->
            Log.d("Index of LazyList: ", index.toString())
            CustomItem(calendarData = calendarItem)

        }
    }
}

@Composable
fun OrGoToReport(
    onReportClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.or),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = stronglyDeemphasizedAlpha),
            modifier = Modifier.paddingFromBaseline(top = 25.dp)
        )
        OutlinedButton(
            onClick = onReportClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 24.dp),
        ) {
            Text(text = stringResource(id = R.string.open_report))
        }
    }
}

@Preview
@Composable
private fun OrGoToReportPrev() {
    LIONSWeihnachtskalenderTheme {
        Surface {
            OrGoToReport(
                onReportClicked = {}
            )

        }
    }
}