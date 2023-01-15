package bassamalim.hidaya.viewmodel

import android.location.Location
import android.os.CountDownTimer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import bassamalim.hidaya.Screen
import bassamalim.hidaya.helpers.PrayTimes
import bassamalim.hidaya.repository.HomeRepo
import bassamalim.hidaya.state.HomeState
import bassamalim.hidaya.utils.LangUtils
import bassamalim.hidaya.utils.PTUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HomeRepo
): ViewModel() {

    private val isLocated = savedStateHandle.get<Boolean>("is_located") ?: false
    private val coordinates =
        savedStateHandle.get<FloatArray>("coordinates") ?: floatArrayOf(0f, 0f)

    private val _uiState = MutableStateFlow(HomeState(
        telawatRecord = getTelawatRecord(),
        quranPagesRecord = getQuranPagesRecord(),
        todayWerdPage = getTodayWerdPage(),
        isWerdDone = repository.isWerdDone()
    ))
    val uiState = _uiState.asStateFlow()

    private val prayerNames = repository.getPrayerNames()
    private val numeralsLanguage = repository.getNumeralsLanguage()
    private var times: Array<Calendar?> = arrayOfNulls(6)
    private var formattedTimes: List<String> = arrayListOf()
    private var tomorrowFajr: Calendar = Calendar.getInstance()
    private var formattedTomorrowFajr: String = ""
    private var timer: CountDownTimer? = null
    private var upcomingPrayer = 0
    private var tomorrow = false
    var pastTime = 0L
        private set
    var upcomingTime = 0L
        private set
    var remaining = 0L
        private set

    fun onStart() {
        if (isLocated) setupPrayersCard()

        _uiState.update { it.copy(
            telawatRecord = getTelawatRecord(),
            quranPagesRecord = getQuranPagesRecord(),
            todayWerdPage = getTodayWerdPage(),
            isWerdDone = repository.isWerdDone()
        )}
    }

    fun onStop() {
        timer?.cancel()
    }

    fun onGotoTodayWerdClick(navController: NavController) {
        navController.navigate(
            Screen.QuranViewer.withArgs(
                "by_page",
                _uiState.value.todayWerdPage
            )
        )
    }

    private fun setupPrayersCard() {
        val location = Location("")
        location.latitude = coordinates[0].toDouble()
        location.longitude = coordinates[1].toDouble()

        getTimes(location)
        setupUpcomingPrayer()
    }

    private fun getTimes(location: Location) {
        val utcOffset = PTUtils.getUTCOffset(
            pref = repository.pref,
            db = repository.db
        )

        val prayTimes = PrayTimes(repository.pref)

        val today = Calendar.getInstance()
        times = prayTimes.getPrayerTimes(
            location.latitude, location.longitude, utcOffset.toDouble(), today
        )
        formattedTimes = prayTimes.getStrPrayerTimes(
            location.latitude, location.longitude, utcOffset.toDouble(), today
        )

        val tomorrow = Calendar.getInstance()
        tomorrow[Calendar.DATE]++
        tomorrowFajr = prayTimes.getPrayerTimes(
            location.latitude, location.longitude, utcOffset.toDouble(), tomorrow
        )[0]!!
        tomorrowFajr[Calendar.DATE]++
        formattedTomorrowFajr = prayTimes.getStrPrayerTimes(
            location.latitude, location.longitude, utcOffset.toDouble(), tomorrow
        )[0]
    }

    private fun setupUpcomingPrayer() {
        upcomingPrayer = findUpcoming()

        tomorrow = false
        if (upcomingPrayer == -1) {
            tomorrow = true
            upcomingPrayer = 0
        }

        var till = times[upcomingPrayer]!!.timeInMillis
        if (tomorrow) till = tomorrowFajr.timeInMillis

        _uiState.update { it.copy(
            upcomingPrayerName = prayerNames[upcomingPrayer],
            upcomingPrayerTime =
                if (tomorrow) formattedTomorrowFajr
                else formattedTimes[upcomingPrayer]
        )}

        count(till)
    }

    private fun count(till: Long) {
        timer = object : CountDownTimer(
            till - System.currentTimeMillis(), 1000
        ) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (60 * 60 * 1000) % 24
                val minutes = millisUntilFinished / (60 * 1000) % 60
                val seconds = millisUntilFinished / 1000 % 60

                val hms = String.format(
                    Locale.US,
                    "%02d:%02d:%02d",
                    hours, minutes, seconds
                )

                _uiState.update { it.copy(
                    remainingTime = LangUtils.translateNums(
                        numeralsLanguage = numeralsLanguage,
                        string = hms,
                        timeFormat = true
                    )
                )}

                val past =
                    if (upcomingPrayer == 0) -1L
                    else times[upcomingPrayer - 1]!!.timeInMillis

                pastTime = past
                upcomingTime = times[upcomingPrayer]!!.timeInMillis
                remaining = millisUntilFinished
            }

            override fun onFinish() {
                setupUpcomingPrayer()
            }
        }.start()
    }

    private fun findUpcoming(): Int {
        val currentMillis = System.currentTimeMillis()
        for (i in times.indices) {
            val millis = times[i]!!.timeInMillis
            if (millis > currentMillis) return i
        }
        return -1
    }

    private fun getTelawatRecord(): String {
        val millis = repository.getTelawatPlaybackRecord()

        val hours = millis / (60 * 60 * 1000) % 24
        val minutes = millis / (60 * 1000) % 60
        val seconds = millis / 1000 % 60

        return LangUtils.translateNums(
            numeralsLanguage,
            String.format(
                Locale.US, "%02d:%02d:%02d",
                hours, minutes, seconds
            )
        )
    }

    private fun getQuranPagesRecord(): String {
        return LangUtils.translateNums(
            numeralsLanguage = numeralsLanguage,
            string = repository.getQuranPagesRecord().toString()
        )
    }

    private fun getTodayWerdPage(): String {
        return LangUtils.translateNums(
            numeralsLanguage = numeralsLanguage,
            string = repository.getTodayWerdPage().toString()
        )
    }

}