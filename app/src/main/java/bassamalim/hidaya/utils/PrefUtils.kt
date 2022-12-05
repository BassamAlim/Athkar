package bassamalim.hidaya.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import bassamalim.hidaya.R
import bassamalim.hidaya.enums.PID

object PrefUtils {

    fun getLanguage(
        context: Context,
        pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    ): String {
        return pref.getString(
            context.getString(R.string.language_key), context.getString(R.string.default_language)
        )!!
    }

    fun getNumeralsLanguage(
        context: Context,
        pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    ): String {
        return pref.getString(
            context.getString(R.string.numerals_language_key),
            context.getString(R.string.default_language)
        )!!
    }

    fun getTimeFormat(
        context: Context,
        pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    ): String {
        return pref.getString(
            context.getString(R.string.time_format_key),
            context.getString(R.string.default_time_format)
        )!!
    }

    fun getTheme(
        context: Context,
        pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    ) : String {

        return pref.getString(
            context.getString(R.string.theme_key),
            context.getString(R.string.default_theme)
        )!!
    }

    fun keyResIdGetter(pid: PID): Int {
        return when (pid) {
            PID.MORNING -> R.string.morning_athkar_key
            PID.EVENING -> R.string.evening_athkar_key
            PID.DAILY_WERD -> R.string.daily_werd_key
            PID.FRIDAY_KAHF -> R.string.friday_kahf_key
            else -> 0
        }
    }

}