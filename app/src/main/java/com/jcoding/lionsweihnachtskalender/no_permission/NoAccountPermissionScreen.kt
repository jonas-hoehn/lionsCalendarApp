@file:OptIn(ExperimentalMaterial3Api::class)

package com.jcoding.lionsweihnachtskalender.no_permission

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.LIONSWeihnachtskalenderTheme
import com.jcoding.lionsweihnachtskalender.R

@Composable
fun NoAccountPermissionScreen(
    modifier: Modifier = Modifier,
    onNavUp: () -> Unit,
) {

    Scaffold (
        modifier = modifier,
        topBar = {
            NoAccountPermissionTopBar(
                topAppBarText = stringResource(id = R.string.requestAccount),
                onNavUp = onNavUp,
            )
        },

    ){ contenPadding ->
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contenPadding),
            contentAlignment = Alignment.Center
        ){
            Text(text = stringResource(id = R.string.requestAccountDetail))
        }
    }
}

@Composable
fun NoAccountPermissionTopBar(
    topAppBarText: String,
    onNavUp: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = topAppBarText,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavUp
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(68.dp))
        }
    )
}

@Preview
@Composable
private fun NoAccountPermissionPrev() {
    LIONSWeihnachtskalenderTheme {
        NoAccountPermissionScreen(onNavUp = {})

    }
}