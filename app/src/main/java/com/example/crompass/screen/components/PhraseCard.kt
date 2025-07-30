package com.example.crompass.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crompass.ui.theme.CroatianBlue
import com.example.crompass.ui.theme.CroatianWhite

@Composable
fun PhraseCard(
    phraseText: String,
    translation: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CroatianWhite),
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = phraseText,
                style = MaterialTheme.typography.titleLarge,
                color = CroatianBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "➡️ $translation",
                style = MaterialTheme.typography.bodyLarge,
                color = CroatianBlue
            )
        }
    }
}
