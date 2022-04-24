package com.camelcc.overcooked

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Destinations {
    const val HOME = "home"
    const val DETAIL = "photo/{id}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(context.applicationContext))

    NavHost(navController, startDestination = Destinations.HOME) {
        composable(Destinations.HOME) {
            HomeView(viewModel) { photo ->
                navController.navigate("photo/${photo.id}")
            }
        }
        composable(Destinations.DETAIL) { backStackEntry ->
            val detail: Long = requireNotNull(backStackEntry.arguments?.getString("id")).toLong()
            DetailView(detail, viewModel = viewModel) {
                navController.popBackStack()
            }
        }
    }
}