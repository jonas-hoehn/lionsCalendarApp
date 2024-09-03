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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavHostController,
    onReportClicked: () -> Unit
) {


    var showOnboarding by remember { mutableStateOf(false) }

    val viewModel = viewModel<LibraryViewModel>()
    var listSize = 0

    viewModel.listenForScanUpdates { dataSnapshot ->
        CalendarRepository.removeAllData()
        dataSnapshot.children.forEach {
            var calendarData = it.getValue(CalendarData::class.java)
            //FIXME weil unschän
            calendarData?.number = it.key!!?.toInt()!!
            if (calendarData != null) {
                CalendarRepository.addDataEntry(calendarData)
            }
            listSize = CalendarRepository.getAllData().size
        }
    }

    if (showOnboarding) {
        OnboardingScreen(navController, updateShowOnboarding = {
            showOnboarding = it
        })
    } else {
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

    LaunchedEffect(Unit) {
        delay(1000)
        showShimmer = false
        updateShowOnboarding(currentCalendarDataList.isEmpty())
    }



    val viewModel = viewModel<LibraryViewModel>()
    val isLoading by viewModel.isLoading.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    var isRefreshing by remember {
        mutableStateOf(false)
    }

    var isSuccessfullyDeleted by remember {
        mutableStateOf(true)
    }

    val onRefresh = {
        isRefreshing = true
        viewModel.startLoading()
        isRefreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = onRefresh)



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
                                .padding(vertical = 4.dp)
                        ) {

                            Box(modifier = Modifier.pullRefresh(refreshState)) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                itemsIndexed(
                                    items = currentCalendarDataList,
                                    key = { index, calendarData ->
                                        calendarData.number
                                    }
                                )
                                { index, calendarItem ->
                                    Log.d("Index of LazyList: ", index.toString())
                                    SwipeToDeleteContainer(
                                        item = calendarItem,
                                        onDeleted = { removingCalendarData ->
                                            isSuccessfullyDeleted = viewModel.deleteCalendarItem(
                                                removingCalendarData,
                                                calendarItem.number
                                            )
                                            if (!isSuccessfullyDeleted) {
                                                Toast.makeText(
                                                    context,
                                                    "Fehler beim Löschen",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }
                                    ) { cItem ->
                                        CalendarItem(calendarData = cItem)
                                    }

                                }
                            }


                                PullRefreshIndicator(
                                    isRefreshing,
                                    refreshState,
                                    Modifier.align(Alignment.TopCenter)
                                )

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
        color = MaterialTheme.colorScheme.surfaceContainer,
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
                        text = if (calendarData.scanned) "Eingelöst" else "Nicht eingelöst",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                }

                if (expanded) {
                    Text(text = calendarData.date)
                    Text(text = calendarData.time)
                    Text(text = calendarData.scanned.toString())
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