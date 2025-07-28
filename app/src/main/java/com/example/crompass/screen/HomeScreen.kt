package com.example.crompass.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.crompass.R
import com.example.crompass.ui.theme.*

@Composable
fun HomeScreen(navController: NavHostController, rootNavController: NavHostController) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CroatianGray)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_crompass),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(vertical =8.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(0.dp, 20.dp)
            )
        }

        Column(

            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeButton(stringResource(R.string.phrases), "phrases", Icons.Default.MailOutline, navController = navController)
                HomeButton(stringResource(R.string.culture), "culture", Icons.Default.Person, navController = navController)
                HomeButton(stringResource(R.string.translate), "translator", Icons.Default.Face, navController = navController)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeButton(stringResource(R.string.reviews), "review", Icons.Default.Star, navController = navController, iconColor = CroatianBlue)
                HomeButton(stringResource(R.string.emergency), "emergency", Icons.Default.Warning, navController = navController, iconColor = CroatianBlue)
                HomeButton(stringResource(R.string.settings), "settings", Icons.Default.Settings, navController = navController, iconColor = CroatianBlue)
            }
        }
    }
}


@Composable
fun HomeButton(
    label: String,
    route: String,
    icon: ImageVector,
    navController: NavHostController,
    iconColor: Color = CroatianRed
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                navController.navigate(route)
            }
            .padding(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = label,
            color = iconColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}