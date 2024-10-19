@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender.library

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compose.LIONSWeihnachtskalenderTheme
import com.example.compose.stronglyDeemphasizedAlpha
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.jcoding.lionsweihnachtskalender.CustomItem
import com.jcoding.lionsweihnachtskalender.Destinations
import com.jcoding.lionsweihnachtskalender.R
import com.jcoding.lionsweihnachtskalender.data.CalendarData
import com.jcoding.lionsweihnachtskalender.effects.AnimatedShimmer
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
import com.jcoding.lionsweihnachtskalender.signinsignup.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavHostController,
    onReportClicked: () -> Unit
) {
    var showOnboarding by remember { mutableStateOf(false) }

    val viewModel = viewModel<LibraryViewModel>()
    var listSize = 0

    val userRole = UserRepository.getManagedUser().role

    viewModel.listenForScanUpdates { dataSnapshot ->
        CalendarRepository.removeAllData()
        dataSnapshot.children.forEach {
            val calendarData = it.getValue(CalendarData::class.java)
            //FIXME weil unschän
            calendarData?.number = it.key!!.toInt()
            if (calendarData != null) {
                CalendarRepository.run { addDataEntry(calendarData) }
            }
            listSize = CalendarRepository.getAllData().size
        }
    }

    if (showOnboarding) {
        OnboardingScreen(navController, updateShowOnboarding = {
            showOnboarding = it
        })
    } else {

        if(userRole != "admin"){ //FIXME sobald Liste unten implementiert ist
            //Liste die auch Einträge löschen kann


        } else{
            HandleList(
                navController,
                onReportClicked,
                listSize,
                currentCalendarDataList = CalendarRepository.getAllData(),
                showOnboarding = showOnboarding,
                updateShowOnboarding = { newValue ->
                    showOnboarding = newValue
                }
            )
        }


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
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDeleted: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    val viewModel = viewModel<LibraryViewModel>()
    var isRemoved by remember { mutableStateOf(false) }
    val state = rememberDismissState(
        confirmStateChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                isRemoved = true
                true
            } else false
        }
    )

    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDeleted(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {

        SwipeToDismiss(
            state = state,
            background = {
                viewModel.DeleteBackground(swipeDismissState = state)
            },
            dismissContent = { content(item) },
            directions = setOf(DismissDirection.EndToStart)
        )
    }

}

@Preview
@Composable
private fun prevHandleList(
    navController: NavHostController = rememberNavController(),
    onReportClicked: () -> Unit = {},
    listSize: Int = 100, // Set a default list size
    currentCalendarDataList: List<CalendarData> = (1..listSize).map {
        CalendarData(
            number = it,
            date = "2024-10-19",
            time = "12:38:49",
            cashier = "TestCashier",
            timestamp = System.currentTimeMillis()
        )
    }, // Create a list of CalendarData objects
    showOnboarding: Boolean = false,
    updateShowOnboarding: (Boolean) -> Unit = {}
) {
    HandleList(
        navController = navController,
        onReportClicked = onReportClicked,
        listSize = listSize,
        currentCalendarDataList = currentCalendarDataList,
        showOnboarding = showOnboarding,
        updateShowOnboarding = updateShowOnboarding
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HandleList(
    navController: NavHostController,
    onReportClicked: () -> Unit,
    listSize: Int,
    currentCalendarDataList: List<CalendarData>,
    showOnboarding: Boolean,
    updateShowOnboarding: (Boolean) -> Unit,
) {

    val context: Context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showShimmer by remember {
        mutableStateOf(true)
    }

    var isRefreshing by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(1000)
        showShimmer = false
        updateShowOnboarding(currentCalendarDataList.isEmpty())
    }


    val viewModel = viewModel<LibraryViewModel>()


    var isSuccessfullyDeleted by remember {
        mutableStateOf(true)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
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
            FloatingActionButton(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    isRefreshing = true
                }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh Icon")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column {
            if (showShimmer) {
                repeat(10) {
                    AnimatedShimmer()
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = innerPadding.calculateTopPadding())
                    ) {

                            PullToRefreshLazyColumn(
                                items = currentCalendarDataList,
                                content = { calendarData ->
                                    CalendarItem(calendarData = calendarData)
                                },
                                isRefreshing = isRefreshing,
                                onRefresh = {
                                    scope.launch {
                                        isRefreshing = true
                                        delay(3000) //simulated API Call
                                        isRefreshing = false
                                    }
                                }
                            )

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
        targetValue = if (expanded) 4.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 1000
        )
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = extraPadding.coerceAtLeast(0.dp)),
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {

                    Text(
                        text = "#" + "${calendarData.number}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = calendarData.date,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = calendarData.time)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = calendarData.cashier)
                }

                if (expanded) {
                    Text(
                        text = calendarData.cashier
                    )
                }
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
            
           // val context = LocalContext.current
            Text(text = stringResource(id = R.string.open_report))
           // Toast.makeText(context, "Button clicked!!", Toast.LENGTH_SHORT).show()
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