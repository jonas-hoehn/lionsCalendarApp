@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.LIONSWeihnachtskalenderTheme
import com.example.compose.stronglyDeemphasizedAlpha
import com.jcoding.lionsweihnachtskalender.CustomItem
import com.jcoding.lionsweihnachtskalender.R
import com.jcoding.lionsweihnachtskalender.effects.AnimatedShimmer
import com.jcoding.lionsweihnachtskalender.repository.CalendarRepository
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(onReportClicked: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showShimmer by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        delay(1000)
        showShimmer = false
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
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Icon"
                        )
                    }
                },
                title = {
                    Text(text = "Library")
                },
            )
        },
    ) { innerPadding ->

        Column {
            if (showShimmer){
                repeat(10){
                    AnimatedShimmer()
                }
            }else{
                CustomList(CalendarRepository, paddingValues = innerPadding)
                //ImageCard(title = "Test", description = "Test")
            }
        }


    }
}

@Composable
fun CustomList(calendarRepository : CalendarRepository, paddingValues: PaddingValues) {


    val getAllData = calendarRepository.getAllData()

    LazyColumn(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .padding(bottom = paddingValues.calculateBottomPadding())
            .padding(start = 12.dp, end = 12.dp, top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(
            items = getAllData,
            key = {index, calendarData ->
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
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
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
        Surface{
            OrGoToReport(
                onReportClicked = {}
            )

        }
    }
}