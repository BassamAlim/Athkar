package bassamalim.hidaya.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.preference.PreferenceManager
import bassamalim.hidaya.database.AppDatabase
import bassamalim.hidaya.enum.PID
import bassamalim.hidaya.helpers.Keeper
import bassamalim.hidaya.helpers.PrayTimes
import bassamalim.hidaya.other.Global
import java.text.SimpleDateFormat
import java.util.*

object PTUtils {

    fun getTimes(
        context: Context,
        loc: Location? = Keeper(context).retrieveLocation(),
        calendar: Calendar = Calendar.getInstance()
    ): Array<Calendar?>? {
        if (loc == null) return null

        val prayTimes = PrayTimes(context)
        val utcOffset = getUTCOffset(context).toDouble()

        return prayTimes.getPrayerTimes(loc.latitude, loc.longitude, utcOffset, calendar)
    }

    fun getStrTimes(
        context: Context,
        loc: Location? = Keeper(context).retrieveLocation(),
        calendar: Calendar = Calendar.getInstance()
    ): ArrayList<String>? {
        if (loc == null) return null

        val pref = PrefUtils.getPreferences(context)

        val prayTimes = PrayTimes(context)
        val utcOffset = getUTCOffset(context, pref).toDouble()

        return prayTimes.getStrPrayerTimes(
            loc.latitude, loc.longitude, utcOffset, calendar
        )
    }

    fun getUTCOffset(
        context: Context,
        pref: SharedPreferences = PrefUtils.getPreferences(context),
        db: AppDatabase = DBUtils.getDB(context)
    ): Int {
        when (PrefUtils.getString(pref, "location_type", "auto")) {
            "auto" -> return TimeZone.getDefault().getOffset(Date().time) / 3600000
            "manual" -> {
                val cityId = PrefUtils.getInt(pref, "city_id", -1)

                if (cityId == -1) return 0

                val timeZoneId = db.cityDao().getCity(cityId).timeZone

                val timeZone = TimeZone.getTimeZone(timeZoneId)
                return timeZone.getOffset(Date().time) / 3600000
            }
            else -> return 0
        }
    }

    fun cancelAlarm(gContext: Context, PID: PID) {
        val pendingIntent = PendingIntent.getBroadcast(
            gContext, PID.ordinal, Intent(),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val am: AlarmManager = gContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent)

        Log.i(Global.TAG, "Canceled $PID Alarm")
    }

    fun formatTime(context: Context, gStr: String): String {
        var str = gStr

        val hour = "%d".format(str.split(':')[0].toInt())
        var minute = str.split(":")[1]
        minute = minute.replace("am", "")
        minute = minute.replace("pm", "")
        minute = minute.replace("ص", "")
        minute = minute.replace("م", "")
        minute = "%02d".format(minute.toInt())

        str = "$hour:$minute"

        val timeFormat = PrefUtils.getTimeFormat(context)

        val h12Format = SimpleDateFormat("hh:mm aa", Locale.US)
        val h24Format = SimpleDateFormat("HH:mm", Locale.US)

        if (str[str.length-1].isDigit()) {  // Input is in 24h format
            return if (timeFormat == "24h") str
            else {
                val date = h24Format.parse(str)
                val output = h12Format.format(date!!).lowercase()
                output
            }
        }
        else { // Input is in 12h format
            return if (timeFormat == "12h") str
            else {
                val date = h12Format.parse(str)
                val output = h24Format.format(date!!)
                output
            }
        }
    }

}