package com.jcoding.lionsweihnachtskalender.overview

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.LIONSWeihnachtskalenderTheme
import com.example.compose.jetsurvey.signinsignup.User
import com.example.compose.jetsurvey.signinsignup.UserRepository
import com.example.compose.stronglyDeemphasizedAlpha
import com.google.firebase.auth.FirebaseAuth
import com.jcoding.lionsweihnachtskalender.R
import com.jcoding.lionsweihnachtskalender.library.OrGoToReport

lateinit var auth: FirebaseAuth

@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    onReportClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onHomeClicked: () -> Unit,
) {


    //Variable declarations here
    var showBranding by rememberSaveable { mutableStateOf(true) }

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
                text = stringResource(id = R.string.go_to_homescreeen),
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
fun OrLogoutFromApp(onLogoutClicked: () -> Unit,
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
            onReportClicked = {},
            onLogoutClicked = {},
            onHomeClicked = {},
        )
    }

}