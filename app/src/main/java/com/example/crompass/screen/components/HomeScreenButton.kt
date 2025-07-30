package com.example.crompass.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.crompass.ui.theme.CroatianWhite

@Composable
fun HomeScreenButton(
    label: String,
    route: String,
    color: Color,
    icon: ImageVector,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(CroatianWhite, RoundedCornerShape(16.dp))
            .clickable { navController.navigate(route) }
            .size(100.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val isSingleWord = !label.contains(" ")
            val adjustedFontSize = if (label.length > 10 && isSingleWord) 12.sp else 16.sp

            Text(
                text = label,
                style = TextStyle(
                    color = color,
                    fontSize = adjustedFontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                maxLines = if (isSingleWord) 1 else 2,
                softWrap = !isSingleWord,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
