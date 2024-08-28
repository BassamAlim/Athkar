package bassamalim.hidaya.core.data.preferences.migrations

import android.content.Context
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import bassamalim.hidaya.core.data.preferences.Preference
import bassamalim.hidaya.core.data.preferences.PreferencesFileNames
import bassamalim.hidaya.core.data.preferences.objects.PrayersPreferences
import bassamalim.hidaya.core.enums.HighLatitudesAdjustmentMethod
import bassamalim.hidaya.core.enums.PID
import bassamalim.hidaya.core.enums.PrayerTimeCalculationMethod
import bassamalim.hidaya.core.enums.PrayerTimeJuristicMethod
import bassamalim.hidaya.core.models.PrayerTimeCalculatorSettings
import kotlinx.collections.immutable.persistentMapOf

object PrayersPreferencesMigration {

    fun getMigration(context: Context) =
        SharedPreferencesMigration(
            context = context,
            sharedPreferencesName = PreferencesFileNames.PRAYERS_PREFERENCES_NAME
        ) { sharedPrefs: SharedPreferencesView, currentData: PrayersPreferences ->
            currentData.copy(
                prayerTimeCalculatorSettings = PrayerTimeCalculatorSettings(
                    calculationMethod = PrayerTimeCalculationMethod.valueOf(
                        sharedPrefs.getString(
                            key = Preference.PrayerTimesCalculationMethod.key,
                            defValue = Preference.PrayerTimesCalculationMethod.default as String
                        )!!
                    ),
                    juristicMethod = PrayerTimeJuristicMethod.valueOf(
                        sharedPrefs.getString(
                            key = Preference.PrayerTimesJuristicMethod.key,
                            defValue = Preference.PrayerTimesJuristicMethod.default as String
                        )!!
                    ),
                    highLatitudesAdjustmentMethod = HighLatitudesAdjustmentMethod.valueOf(
                        sharedPrefs.getString(
                            key = Preference.PrayerTimesAdjustment.key,
                            defValue = Preference.PrayerTimesAdjustment.default as String
                        )!!
                    )
                ),
                timeOffsets = persistentMapOf(
                    PID.FAJR to sharedPrefs.getInt(
                        key = Preference.TimeOffset(PID.FAJR).key,
                        defValue = Preference.TimeOffset(PID.FAJR).default as Int
                    ),
                    PID.SUNRISE to sharedPrefs.getInt(
                        key = Preference.TimeOffset(PID.SUNRISE).key,
                        defValue = Preference.TimeOffset(PID.SUNRISE).default as Int
                    ),
                    PID.DHUHR to sharedPrefs.getInt(
                        key = Preference.TimeOffset(PID.DHUHR).key,
                        defValue = Preference.TimeOffset(PID.DHUHR).default as Int
                    ),
                    PID.ASR to sharedPrefs.getInt(
                        key = Preference.TimeOffset(PID.ASR).key,
                        defValue = Preference.TimeOffset(PID.ASR).default as Int
                    ),
                    PID.MAGHRIB to sharedPrefs.getInt(
                        key = Preference.TimeOffset(PID.MAGHRIB).key,
                        defValue = Preference.TimeOffset(PID.MAGHRIB).default as Int
                    ),
                    PID.ISHAA to sharedPrefs.getInt(
                        key = Preference.TimeOffset(PID.ISHAA).key,
                        defValue = Preference.TimeOffset(PID.ISHAA).default as Int
                    ),
                ),
                athanId = sharedPrefs.getString(
                    key = Preference.AthanId.key,
                    defValue = Preference.AthanId.default as String
                )!!.toInt(),
                shouldShowTutorial = sharedPrefs.getBoolean(
                    key = Preference.ShowPrayersTutorial.key,
                    defValue = Preference.ShowPrayersTutorial.default as Boolean
                ),
            )
        }

}