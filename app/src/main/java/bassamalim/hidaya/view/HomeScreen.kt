package bassamalim.hidaya.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import bassamalim.hidaya.viewmodel.HomeVM

@Composable
fun HomeUI(
    navController: NavController = rememberNavController(),
    viewModel: HomeVM = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
}