package bassamalim.hidaya.features.main.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import bassamalim.hidaya.R
import bassamalim.hidaya.core.ui.components.MyText
import bassamalim.hidaya.core.ui.theme.nsp

sealed class BottomNavItem(var route: String, var titleRId: Int, var icon: Int) {
    data object Home: BottomNavItem("home", R.string.title_home, R.drawable.ic_home)
    data object PrayersBoard: BottomNavItem("prayers", R.string.title_prayers, R.drawable.ic_clock)
    data object QuranSuras: BottomNavItem("quran", R.string.title_quran, R.drawable.ic_bar_quran)
    data object RemembranceCategories: BottomNavItem("remembrances", R.string.title_remembrances, R.drawable.ic_duaa)
    data object More: BottomNavItem("more", R.string.title_more, R.drawable.ic_more)
}

@Composable
fun MyBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.PrayersBoard,
        BottomNavItem.QuranSuras,
        BottomNavItem.RemembranceCategories,
        BottomNavItem.More
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val title = stringResource(item.titleRId)

            NavigationBarItem(
                label = {
                    MyText(
                        text = title,
                        fontSize = 9.nsp,
                        softWrap = false
                    )
                },
                alwaysShowLabel = true,
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = title,
                        modifier = Modifier.size(24.dp),
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}