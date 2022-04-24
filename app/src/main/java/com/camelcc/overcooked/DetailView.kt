package com.camelcc.overcooked

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailView(id: Long, viewModel: MainViewModel, onBack: () -> Unit) {
    val photo = viewModel.photoBy(id)!!
    Scaffold(topBar = {
        SmallTopAppBar(title = { }, navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = ""
                )
            }
        })
    }) {
        AsyncImage(model = photo.src.large2x, contentDescription = "")
    }
}