package com.example.crompass.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.crompass.R
import com.example.crompass.screen.components.HomeScreenButton
import com.example.crompass.ui.theme.*

@Composable
fun HomeScreen(navController: NavHostController, rootNavController: NavHostController) {
    val scrollState = rememberScrollState()
    val boxShape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CroatianGray)
            .verticalScroll(scrollState)
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(boxShape)
                .background(CroatianRed)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_crompass),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(270.dp)
                    .padding(0.dp, 0.dp)
                    .align(Alignment.Center)
            )
        }

        Column(

            modifier = Modifier.padding(vertical = 50.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeScreenButton(
                    label = stringResource(R.string.phrases),
                    route = "phrases",
                    icon = Icons.Filled.ChatBubble,
                    navController = navController,
                    color = CroatianRed
                )
                HomeScreenButton(
                    label = stringResource(R.string.culture),
                    route = "culture",
                    icon = Icons.Filled.Handshake,
                    navController = navController,
                    color = CroatianRed
                )
                HomeScreenButton(
                    label = stringResource(R.string.translate),
                    route = "translator",
                    icon = Icons.Filled.Translate,
                    navController = navController,
                    color = CroatianRed
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HomeScreenButton(
                    label = stringResource(R.string.reviews),
                    route = "review",
                    icon = Icons.Filled.Stars,
                    navController = navController,
                    color = CroatianBlue
                )
                HomeScreenButton(
                    label = stringResource(R.string.emergency),
                    route = "emergency",
                    icon = Icons.Filled.Warning,
                    navController = navController,
                    color = CroatianBlue
                )
                HomeScreenButton(
                    label = stringResource(R.string.settings),
                    route = "settings",
                    icon = Icons.Filled.Settings,
                    navController = navController,
                    color = CroatianBlue
                )
            }
        }
    }
}