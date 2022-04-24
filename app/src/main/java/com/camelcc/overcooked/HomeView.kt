package com.camelcc.overcooked

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: MainViewModel, onPhotoClicked: (Photo) -> Unit) {
    val refreshState by viewModel.loadingFlow.collectAsState()
    val photos by viewModel.photosFlow.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibles ->
                return@map visibles.map { item ->
                    photos[item.index]
                }
            }
            .distinctUntilChanged()
            .filter { true }
            .collect { photos ->
                viewModel.updateVisiblePhotos(photos = photos)
            }
    }

    Scaffold(topBar = {
        AppToolbar(onSearch = {
            viewModel.search(search = it)
        }, onClear = {
            viewModel.resetSearch()
        })
    }) { padding ->
        SwipeRefresh(
            modifier = Modifier.padding(padding),
            state = rememberSwipeRefreshState(isRefreshing = refreshState == LoadingState.Loading),
            onRefresh = { viewModel.refresh() }) {
            LazyColumn(contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState) {
                items(photos.size) { pos ->
                    val photo = photos[pos]
                    PhotoCardView(
                        photo = photo,
                        onClick = {
                            onPhotoClicked(photo)
                        },
                        onToggle = {
                            viewModel.togglePhotoButton(photo = photo)
                        })
                }
            }
        }
    }
}

@Composable
fun AppToolbar(onSearch: (String) -> Unit, onClear: () -> Unit) {
    var searchActivate by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    if (searchActivate) {
        SmallTopAppBar(title = {},
            actions = {
                TextField(
                    value = searchText, onValueChange = { search ->
                        searchText = search
                    },
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        searchActivate = false
                        focusManager.clearFocus()
                        onSearch(searchText)
                    }),
                    trailingIcon = {
                        IconButton(onClick = {
                            searchActivate = false
                            focusManager.clearFocus()
                            onClear()
                        }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }, colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent, //hide the indicator
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            })
    } else {
        SmallTopAppBar(title = { Text("OverCooked") },
            actions = {
            IconButton(onClick = {
                searchActivate = true
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "")
            }
        })
    }
}