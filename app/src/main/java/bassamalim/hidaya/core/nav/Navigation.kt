package bassamalim.hidaya.core.nav

import android.os.Build
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import bassamalim.hidaya.core.ui.inFromBottom
import bassamalim.hidaya.core.ui.inFromLeft
import bassamalim.hidaya.core.ui.inFromRight
import bassamalim.hidaya.core.ui.inFromTop
import bassamalim.hidaya.core.ui.outToBottom
import bassamalim.hidaya.core.ui.outToLeft
import bassamalim.hidaya.core.ui.outToTop
import bassamalim.hidaya.features.about.ui.AboutScreen
import bassamalim.hidaya.features.books.bookChapters.ui.BookChaptersScreen
import bassamalim.hidaya.features.books.bookReader.ui.BookReaderScreen
import bassamalim.hidaya.features.books.bookSearcher.ui.BookSearcherScreen
import bassamalim.hidaya.features.books.booksMenu.ui.BooksMenuScreen
import bassamalim.hidaya.features.dateConverter.ui.DateConverterScreen
import bassamalim.hidaya.features.dateEditor.ui.DateEditorDialog
import bassamalim.hidaya.features.hijriDatePicker.ui.HijriDatePickerDialog
import bassamalim.hidaya.features.leaderboard.ui.LeaderboardScreen
import bassamalim.hidaya.features.locationPicker.ui.LocationPickerScreen
import bassamalim.hidaya.features.locator.ui.LocatorScreen
import bassamalim.hidaya.features.main.ui.MainScreen
import bassamalim.hidaya.features.onboarding.ui.OnboardingScreen
import bassamalim.hidaya.features.prayers.prayerReminderSettings.ui.PrayerReminderSettingsDialog
import bassamalim.hidaya.features.prayers.prayerSettings.ui.PrayerSettingsDialog
import bassamalim.hidaya.features.qibla.ui.QiblaScreen
import bassamalim.hidaya.features.quiz.quizLobby.QuizLobbyScreen
import bassamalim.hidaya.features.quiz.quizResult.ui.QuizResultScreen
import bassamalim.hidaya.features.quiz.quizTest.ui.QuizTestScreen
import bassamalim.hidaya.features.quran.quranReader.ui.QuranReaderScreen
import bassamalim.hidaya.features.quran.quranSearcher.ui.QuranSearcherScreen
import bassamalim.hidaya.features.radio.ui.RadioClientScreen
import bassamalim.hidaya.features.recitations.recitationPlayer.ui.RecitationPlayerScreen
import bassamalim.hidaya.features.recitations.recitationRecitersMenu.ui.RecitationRecitersMenuScreen
import bassamalim.hidaya.features.recitations.recitationSurasMenu.ui.RecitationSurasMenuScreen
import bassamalim.hidaya.features.remembrances.remembranceReader.ui.RemembranceReaderScreen
import bassamalim.hidaya.features.remembrances.remembrancesMenu.ui.RemembrancesMenuScreen
import bassamalim.hidaya.features.settings.ui.SettingsScreen
import bassamalim.hidaya.features.tv.ui.TvScreen
import com.google.gson.Gson

@Composable
fun Navigation(
    navigator: Navigator,
    thenTo: String? = null,
    shouldOnboard: Boolean = false
) {
    val navController = rememberNavController()

    LaunchedEffect(key1 = navController) {  // maybe should be DisposableEffect
        navigator.setController(navController)
//        onDispose {
//            navigator.clear()
//        }
    }

    val startDest =
        if (shouldOnboard) Screen.Onboarding.route
        else Screen.Main.route

    NavGraph(navController, startDest)

    if (thenTo != null) navController.navigate(thenTo)
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDest: String
) {
    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable(
            route = Screen.About.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            AboutScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.RemembrancesMenu("{type}", "{category}").route,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("category") { type = NavType.IntType }
            ),
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            RemembrancesMenuScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.RemembranceReader("{remembrance_id}").route,
            arguments = listOf(
                navArgument("remembrance_id") { type = NavType.IntType }
            ),
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            RemembranceReaderScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.BookChapters("{book_id}", "{book_title}").route,
            arguments = listOf(
                navArgument("book_id") { type = NavType.IntType },
                navArgument("book_title") { type = NavType.StringType }
            ),
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            BookChaptersScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.BookSearcher.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            BookSearcherScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.BooksMenu.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            BooksMenuScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.BookReader(
                "{book_id}", "{book_title}", "{chapter_id}"
            ).route,
            arguments = listOf(
                navArgument("book_id") { type = NavType.IntType },
                navArgument("book_title") { type = NavType.StringType },
                navArgument("chapter_id") { type = NavType.IntType }
            ),
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            BookReaderScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.DateConverter.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            DateConverterScreen(
                hiltViewModel()
            )
        }

        dialog(
            route = Screen.DateEditor.route
        ) {
            DateEditorDialog(
                hiltViewModel()
            )
        }

        dialog(
            route = Screen.HijriDatePicker("{initial_date}").route,
        ) {
            HijriDatePickerDialog(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.Leaderboard.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            LeaderboardScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.LocationPicker.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            LocationPickerScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.Locator("{is_initial}").route,
            arguments = listOf(
                navArgument("is_initial") { type = NavType.BoolType }
            ),
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            LocatorScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.Main.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            MainScreen(
                hiltViewModel()
            )
        }

        dialog(
            route = Screen.PrayerReminderSettings("{pid}").route,
            arguments = listOf(
                navArgument("pid") { type = NavType.StringType }
            )
        ) {
            PrayerReminderSettingsDialog(
                hiltViewModel()
            )
        }

        dialog(
            route = Screen.PrayerSettings("{pid}").route,
            arguments = listOf(
                navArgument("pid") { type = NavType.StringType }
            )
        ) {
            PrayerSettingsDialog(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.Qibla.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            QiblaScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.QuizLobby.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            QuizLobbyScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.QuizResult(
                "{score}", "{questions}", "{chosen_answers}"
            ).route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("questions") { type = IntArrType },
                navArgument("chosen_answers") { type = IntArrType }
            ),
            enterTransition = inFromLeft,
            exitTransition = outToLeft,
            popEnterTransition = inFromRight,
            popExitTransition = outToBottom
        ) {
            QuizResultScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.QuizTest.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            QuizTestScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.QuranSearcher.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            QuranSearcherScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.QuranReader("{target_type}", "{target_value}").route,
            arguments = listOf(
                navArgument("target_type") { type = NavType.StringType },
                navArgument("target_value") { type = NavType.IntType },
            ),
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            QuranReaderScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.RadioClient.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                RadioClientScreen(
                    hiltViewModel()
                )
            }
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            SettingsScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.RecitationPlayer("{action}", "{media_id}").route,
            arguments = listOf(
                navArgument("action") { type = NavType.StringType },
                navArgument("media_id") { type = NavType.StringType }
            ),
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                RecitationPlayerScreen(
                    hiltViewModel()
                )
            }
        }

        composable(
            route = Screen.RecitationsRecitersMenu.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            RecitationRecitersMenuScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.RecitationSurasMenu(
                "{reciter_id}", "{narration_id}"
            ).route,
            arguments = listOf(
                navArgument("reciter_id") { type = NavType.IntType },
                navArgument("narration_id") { type = NavType.IntType }
            ),
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            RecitationSurasMenuScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.Tv.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            TvScreen(
                hiltViewModel()
            )
        }

        composable(
            route = Screen.Onboarding.route,
            enterTransition = inFromBottom,
            exitTransition = outToBottom,
            popEnterTransition = inFromTop,
            popExitTransition = outToTop
        ) {
            OnboardingScreen(
                hiltViewModel()
            )
        }
    }
}


// custom nav type because the default one crashes
val IntArrType: NavType<IntArray> = object : NavType<IntArray>(false) {
    override fun put(bundle: Bundle, key: String, value: IntArray) {
        bundle.putIntArray(key, value)
    }

    override fun get(bundle: Bundle, key: String): IntArray {
        return bundle.getIntArray(key) as IntArray
    }

    override fun parseValue(value: String): IntArray {
        return Gson().fromJson(value, IntArray::class.java)
    }
}