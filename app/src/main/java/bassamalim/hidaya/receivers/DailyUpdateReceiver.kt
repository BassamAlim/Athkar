package bassamalim.hidaya.receivers

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import bassamalim.hidaya.helpers.Alarms
import bassamalim.hidaya.helpers.Keeper
import bassamalim.hidaya.other.Global
import bassamalim.hidaya.other.PrayersWidget
import bassamalim.hidaya.utils.DBUtils
import bassamalim.hidaya.utils.PTUtils
import com.google.android.gms.location.LocationServices
import java.util.*

class DailyUpdateReceiver : BroadcastReceiver() {

    private lateinit var context: Context
    private lateinit var pref: SharedPreferences
    private var hour = 0
    private var minute = 0
    private var now = Calendar.getInstance()

    override fun onReceive(gContext: Context, intent: Intent) {
        Log.i(Global.TAG, "in DailyUpdateReceiver")

        context = gContext
        pref = PreferenceManager.getDefaultSharedPreferences(context)

        if (intent.action == "daily") {
            hour = intent.getIntExtra("hour", 0)
            minute = intent.getIntExtra("minute", 0)

            if (needed()) {
                when (pref.getString("location_type", "auto")) {
                    "auto" -> locate()
                    "manual" -> {
                        val cityId = pref.getInt("city_id", -1)

                        if (cityId == -1) return

                        val city = DBUtils.getDB(context).cityDao().getCity(cityId)

                        val location = Location("")
                        location.latitude = city.latitude
                        location.longitude = city.longitude
                        update(location)
                    }
                    "none" -> return
                }
            }
            else Log.i(Global.TAG, "dead intent walking in daily update receiver")
        }
        else if (intent.action == "boot") locate()
    }

    private fun needed(): Boolean {
        val day: Int = pref.getInt("last_day", 0)

        val time = Calendar.getInstance()
        time[Calendar.HOUR_OF_DAY] = hour
        time[Calendar.MINUTE] = minute

        return day != now[Calendar.DATE] && time.timeInMillis <= now.timeInMillis
    }

    private fun locate() {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            LocationServices.getFusedLocationProviderClient(context)
                .lastLocation.addOnSuccessListener {
                    location: Location? -> update(location)
            }
        }
    }

    private fun update(location: Location?) {
        var loc: Location? = location
        if (loc == null) {
            loc = Keeper(context).retrieveLocation()
            if (loc == null) {
                Log.e(Global.TAG, "No available location in DailyUpdate")
                return
            }
        }

        Keeper(context, loc)

        val times = PTUtils.getTimes(context, loc)

        Alarms(context, times)

        updateWidget()

        updated()
    }

    private fun updateWidget() {
        val intent = Intent(context, PrayersWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids: IntArray = AppWidgetManager.getInstance(context.applicationContext)
            .getAppWidgetIds(ComponentName(context.applicationContext, PrayersWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    private fun updated() {
        val str = "Last Daily Update: ${now[Calendar.YEAR]}/${now[Calendar.MONTH]}" +
            "/${now[Calendar.DATE]}" + " ${now[Calendar.HOUR_OF_DAY]}:${now[Calendar.MINUTE]}"

        val editor: SharedPreferences.Editor = pref.edit()
        editor.putInt("last_day", now[Calendar.DATE])
        editor.putString("last_daily_update", str)
        editor.apply()
    }

}