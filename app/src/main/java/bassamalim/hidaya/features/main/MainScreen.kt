package bassamalim.hidaya.features.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import bassamalim.hidaya.R
import bassamalim.hidaya.core.ui.*
import bassamalim.hidaya.core.ui.components.*
import bassamalim.hidaya.core.ui.theme.AppTheme
import bassamalim.hidaya.core.ui.theme.nsp
import bassamalim.hidaya.features.athkar.AthkarUI
import bassamalim.hidaya.features.home.HomeUI
import bassamalim.hidaya.features.more.MoreUI
import bassamalim.hidaya.features.prayers.PrayersUI
import bassamalim.hidaya.features.quran.QuranUI
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainUI(
    vm: MainVM,
    nc: NavHostController = rememberAnimatedNavController()
) {
    val st by vm.uiState.collectAsStateWithLifecycle()
    val bottomNc = rememberAnimatedNavController()

    MyScaffold(
        title = stringResource(R.string.app_name),
        topBar = {
            TopAppBar(
                backgroundColor = AppTheme.colors.primary,
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier.fillMaxSize()
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MyText(
                            stringResource(R.string.app_name),
                            textColor = AppTheme.colors.onPrimary
                        )

                        Column(
                            Modifier
                                .fillMaxHeight()
                                .clickable { vm.showDateEditor() },
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(
                                Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 10.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                MyText(
                                    text = st.hijriDate,
                                    fontSize = 16.nsp,
                                    fontWeight = FontWeight.Bold,
                                    textColor = AppTheme.colors.onPrimary
                                )

                                MyText(
                                    text = st.gregorianDate,
                                    fontSize = 16.nsp,
                                    textColor = AppTheme.colors.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = { MyBottomNavigation(bottomNc) }
    ) {
        NavigationGraph(nc, bottomNc, it)

        DateEditorDialog(
            shown = st.dateEditorShown,
            offsetText = st.dateEditorOffsetText,
            dateText = st.dateEditorDateText,
            onNextDay = { vm.onDateEditorNextDay() },
            onPreviousDay = { vm.onDateEditorPrevDay() },
            onCancel = { vm.onDateEditorCancel() },
            onSubmit = { vm.onDateEditorSubmit() }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    bottomNavController: NavHostController,
    padding: PaddingValues
) {
    AnimatedNavHost(
        bottomNavController,
        startDestination = BottomNavItem.Home.route,
        modifier = Modifier.padding(padding)
    ) {
        composable(
            route = BottomNavItem.Home.route,
            enterTransition = TabEnter,
            exitTransition = TabExit,
            popEnterTransition = TabPopEnter,
            popExitTransition = TabPopExit
        ) {
            HomeUI(
                nc = navController,
                vm = hiltViewModel()
            )
        }

        composable(
            route = BottomNavItem.Prayers.route,
            enterTransition = TabEnter,
            exitTransition = TabExit,
            popEnterTransition = TabPopEnter,
            popExitTransition = TabPopExit
        ) {
            PrayersUI(
                vm = hiltViewModel(),
                nc = navController
            )
        }

        composable(
            route = BottomNavItem.Quran.route,
            enterTransition = TabEnter,
            exitTransition = TabExit,
            popEnterTransition = TabPopEnter,
            popExitTransition = TabPopExit
        ) {
            QuranUI(
                vm = hiltViewModel(),
                nc = navController
            )
        }

        composable(
            route = BottomNavItem.Athkar.route,
            enterTransition = TabEnter,
            exitTransition = TabExit,
            popEnterTransition = TabPopEnter,
            popExitTransition = TabPopExit
        ) {
            AthkarUI(
                viewModel = hiltViewModel(),
                navController = navController
            )
        }

        composable(
            route = BottomNavItem.More.route,
            enterTransition = TabEnter,
            exitTransition = TabExit,
            popEnterTransition = TabPopEnter,
            popExitTransition = TabPopExit
        ) {
            MoreUI(
                vm = hiltViewModel(),
                nc = navController
            )
        }
    }
}