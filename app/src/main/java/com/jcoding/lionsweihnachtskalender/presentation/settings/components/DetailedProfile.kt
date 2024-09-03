package com.jcoding.lionsweihnachtskalender.presentation.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
                modifier = Modifier.padding(contentPadding)
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

@Composable
fun HeaderText() {
    IconButton(
        onClick = {
            //TODO navigiere zum Homescreen
        },
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
    Text(
        text = "Mein Profil",
        color = MaterialTheme.colorScheme.secondary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp, bottom = 10.dp),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp
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
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
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
                    color = MaterialTheme.colorScheme.secondary,
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