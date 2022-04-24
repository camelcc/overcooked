package com.camelcc.overcooked

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCardView(photo: Photo, onClick: () -> Unit, onToggle: () -> Unit) {
    Card(onClick = onClick) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(modifier = Modifier.fillMaxWidth(),
                model = photo.src.large2x,
                contentDescription = photo.title,
                contentScale = ContentScale.FillWidth)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                Text(text = photo.title)
                Spacer(modifier = Modifier.weight(1.0f))
                Text(text = photo.counter.toString(), style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onToggle) {
                    Icon(imageVector = if (photo.enabled) Icons.Default.Close else Icons.Default.PlayArrow,
                        contentDescription = "")
                }
            }
        }
    }
}