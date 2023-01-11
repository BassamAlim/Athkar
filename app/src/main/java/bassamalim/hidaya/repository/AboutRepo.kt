package bassamalim.hidaya.repository

import android.content.SharedPreferences
import bassamalim.hidaya.utils.PrefUtils
import javax.inject.Inject

class AboutRepo @Inject constructor(
    private val pref: SharedPreferences
) {

    fun getLastUpdate(): String {
        return PrefUtils.getString(
            pref,
            "last_daily_update",
            "No daily updates yet"
        )
    }

}