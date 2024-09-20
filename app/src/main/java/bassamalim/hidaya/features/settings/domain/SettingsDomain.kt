package bassamalim.hidaya.features.settings.domain

import android.app.Activity
import bassamalim.hidaya.core.data.repositories.AppSettingsRepository
import bassamalim.hidaya.core.data.repositories.LocationRepository
import bassamalim.hidaya.core.data.repositories.NotificationsRepository
import bassamalim.hidaya.core.data.repositories.PrayersRepository
import bassamalim.hidaya.core.enums.HighLatitudesAdjustmentMethod
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.enums.PrayerTimeCalculationMethod
import bassamalim.hidaya.core.enums.PrayerTimeJuristicMethod
import bassamalim.hidaya.core.enums.Reminder
import bassamalim.hidaya.core.enums.Theme
import bassamalim.hidaya.core.enums.TimeFormat
import bassamalim.hidaya.core.helpers.Alarm
import bassamalim.hidaya.core.models.TimeOfDay
import bassamalim.hidaya.core.utils.ActivityUtils
import bassamalim.hidaya.core.utils.PrayerTimeUtils
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class SettingsDomain @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val prayersRepository: PrayersRepository,
    private val notificationsRepository: NotificationsRepository,
    private val locationRepository: LocationRepository,
    private val alarm: Alarm
) {

    suspend fun resetPrayerTimes() {
        val location = locationRepository.getLocation().first() ?: return

        val prayerTimes = PrayerTimeUtils.getPrayerTimes(
            settings = prayersRepository.getPrayerTimesCalculatorSettings().first(),
            timeZoneId = locationRepository.getTimeZone(location.ids.cityId),
            location = location,
            calendar = Calendar.getInstance()
        )

        alarm.setAll(prayerTimes)
    }

    suspend fun setAlarm(reminder: Reminder.Prayer) {
        alarm.setAlarm(reminder)
    }

    fun cancelAlarm(reminder: Reminder.Prayer) {
        alarm.cancelAlarm(reminder)
    }

    fun getLanguage() = appSettingsRepository.getLanguage()

    suspend fun setLanguage(language: Language) {
        appSettingsRepository.setLanguage(language)
    }

    fun getNumeralsLanguage() = appSettingsRepository.getNumeralsLanguage()

    suspend fun setNumeralsLanguage(numeralsLanguage: Language) {
        appSettingsRepository.setNumeralsLanguage(numeralsLanguage)
    }

    fun getTimeFormat() = appSettingsRepository.getTimeFormat()

    suspend fun setTimeFormat(timeFormat: TimeFormat) {
        appSettingsRepository.setTimeFormat(timeFormat)
    }

    fun getTheme() = appSettingsRepository.getTheme()

    suspend fun setTheme(theme: Theme) {
        appSettingsRepository.setTheme(theme)
    }

    fun getDevotionReminderEnabledMap() =
        notificationsRepository.getDevotionalReminderEnabledMap()

    suspend fun setDevotionReminderEnabled(enabled: Boolean, reminder: Reminder.Devotional) {
        notificationsRepository.setDevotionalReminderEnabled(enabled, reminder)
    }

    fun getDevotionReminderTimeOfDayMap() =
        notificationsRepository.getDevotionalReminderTimes()

    suspend fun setDevotionReminderTimeOfDay(timeOfDay: TimeOfDay, reminder: Reminder.Devotional) {
        notificationsRepository.setDevotionalReminderTimes(timeOfDay, reminder)
    }

    fun getPrayerTimesCalculatorSettings() = prayersRepository.getPrayerTimesCalculatorSettings()

    suspend fun setPrayerTimeCalculationMethod(calculationMethod: PrayerTimeCalculationMethod) {
        prayersRepository.setCalculationMethod(calculationMethod)
    }

    suspend fun setPrayerTimeJuristicMethod(juristicMethod: PrayerTimeJuristicMethod) {
        prayersRepository.setJuristicMethod(juristicMethod)
    }

    suspend fun setHighLatitudesAdjustmentMethod(adjustmentMethod: HighLatitudesAdjustmentMethod) {
        prayersRepository.setAdjustHighLatitudes(adjustmentMethod)
    }

    fun getAthanAudioId() = prayersRepository.getAthanAudioId()

    suspend fun setAthanAudioId(athanAudioId: Int) {
        prayersRepository.setAthanAudioId(athanAudioId)
    }

    fun restartActivity(activity: Activity) {
        ActivityUtils.restartActivity(activity)
    }

}