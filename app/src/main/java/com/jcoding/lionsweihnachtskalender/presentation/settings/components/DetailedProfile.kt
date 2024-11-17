package com.jcoding.lionsweihnachtskalender.presentation.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.compose.LIONSWeihnachtskalenderTheme
import com.jcoding.lionsweihnachtskalender.R
import com.jcoding.lionsweihnachtskalender.overview.Logo

@Composable
fun DetailedProfileScreen(
    navController: NavHostController
) {
    Scaffold (
        topBar = {
            HeaderText()
        },
        content = { contentPadding ->
            Column (
                modifier = Modifier
                    .padding(contentPadding)
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxSize()
            ){
                ProfileCardUIDetailed()
            }
        }
    )
}

@Preview
@Composable
fun DetailedProfileScreenPreview() {
    LIONSWeihnachtskalenderTheme {
        DetailedProfileScreen(navController = NavHostController(LocalContext.current))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderText(
    onNavUp: () -> Unit = {},
    topAppBarText: String = stringResource(id = R.string.profileSettings)
) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = topAppBarText,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        },
        navigationIcon = {
            androidx.compose.material3.IconButton(onClick = onNavUp) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        // We need to balance the navigation icon, so we add a spacer.
        actions = {
            Spacer(modifier = Modifier.width(68.dp))
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(MaterialTheme.colorScheme.surfaceContainer)
    )

}

@Composable
fun ProfileCardUIDetailed(
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = CardDefaults.elevatedShape
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Vor- und Zuname",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "useremail@example.com",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )

            }
            Image(
                painter = painterResource(id = R.drawable.lcl_emblem_2color_web),
                contentDescription = "",
                modifier = Modifier.weight(1f)
            )
        }
    }
}