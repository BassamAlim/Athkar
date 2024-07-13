package bassamalim.hidaya.features.home.ui

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.nav.Navigator
import bassamalim.hidaya.core.nav.Screen
import bassamalim.hidaya.core.utils.LangUtils.translateNums
import bassamalim.hidaya.features.home.domain.HomeDomain
import bassamalim.hidaya.features.quranReader.QuranTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val domain: HomeDomain,
    private val navigator: Navigator
): ViewModel() {

    lateinit var numeralsLanguage: Language
    private val prayerNames = domain.getPrayerNames()
    private var times: Array<Calendar?> = arrayOfNulls(6)
    private var formattedTimes: List<String> = arrayListOf()
    private var tomorrowFajr: Calendar = Calendar.getInstance()
    private var formattedTomorrowFajr: String = ""
    private var timer: CountDownTimer? = null
    private var upcomingPrayerIndex = 0
    private var tomorrow = false
    private var counterCounter = 0

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = combine(
        _uiState.asStateFlow(),
        domain.getWerdPage(),
        domain.getIsWerdDone(),
        domain.getLocalRecord()
    ) { state, werdPage, isWerdDone, localRecord ->
        state.copy(
            werdPage = translateNumber(werdPage),
            isWerdDone = isWerdDone,
            quranRecord = translateNumber(localRecord.quranPages),
            recitationsRecord = formatRecitationsTime(localRecord.recitationsTime),
        )
    }.stateIn(
        initialValue = HomeUiState(),
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
    )

    init {
        viewModelScope.launch {
            numeralsLanguage = domain.getNumeralsLanguage()

            val isSuccess = domain.syncRecords()
            if (isSuccess) {
                _uiState.update { it.copy(
                    isLeaderboardEnabled = true
                )}
            }
        }
    }

    fun onStart() {
        viewModelScope.launch {
            if (domain.location.first() != null)
                setupPrayersCard()
        }
    }

    fun onStop() {
        timer?.cancel()
    }

    fun onGotoTodayWerdClick() {
        navigator.navigate(
            Screen.QuranViewer(
                targetType = QuranTarget.PAGE.name,
                targetValue = _uiState.value.werdPage
            )
        )
    }

    fun onLeaderboardClick() {
        navigator.navigate(Screen.Leaderboard)
    }

    private fun setupPrayersCard() {
        viewModelScope.launch {
            times = domain.getTimes()
            formattedTimes = domain.getStrTimes()
            tomorrowFajr = domain.getTomorrowFajr()
            formattedTomorrowFajr = domain.getStrTomorrowFajr()

            setupUpcomingPrayer()
        }
    }

    private fun setupUpcomingPrayer() {
        upcomingPrayerIndex = domain.getUpcomingPrayerIndex(times)

        tomorrow = false
        if (upcomingPrayerIndex == -1) {
            tomorrow = true
            upcomingPrayerIndex = 0
        }

        var till = times[upcomingPrayerIndex]!!.timeInMillis
        if (tomorrow) till = tomorrowFajr.timeInMillis

        _uiState.update { it.copy(
            upcomingPrayerName = prayerNames[upcomingPrayerIndex],
            upcomingPrayerTime =
                if (tomorrow) formattedTomorrowFajr
                else formattedTimes[upcomingPrayerIndex]
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
                    remaining = translateNums(
                        numeralsLanguage = numeralsLanguage,
                        string = hms,
                        timeFormat = true
                    ),
                    timeFromPreviousPrayer =
                        if (upcomingPrayerIndex == 0) -1L
                        else times[upcomingPrayerIndex - 1]!!.timeInMillis,
                    timeToNextPrayer = millisUntilFinished
                )}
            }

            override fun onFinish() {
                counterCounter++
                if (counterCounter < 5)
                    setupPrayersCard()
            }
        }.start()
    }

    private fun formatRecitationsTime(millis: Long): String {
        val hours = millis / (60 * 60 * 1000) % 24
        val minutes = millis / (60 * 1000) % 60
        val seconds = millis / 1000 % 60

        return translateNums(
            numeralsLanguage,
            String.format(
                Locale.US, "%02d:%02d:%02d",
                hours, minutes, seconds
            )
        )
    }

    private fun translateNumber(num: Int) =
        translateNums(
            numeralsLanguage = numeralsLanguage,
            string = num.toString()
        )

}