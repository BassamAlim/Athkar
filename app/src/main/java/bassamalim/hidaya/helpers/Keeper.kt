package bassamalim.hidaya.helpers

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.util.Log
import androidx.preference.PreferenceManager
import bassamalim.hidaya.R
import bassamalim.hidaya.models.MyLocation
import bassamalim.hidaya.other.Global
import bassamalim.hidaya.other.Utils
import com.google.gson.Gson
import java.util.*

class Keeper {

    private val context: Context
    private var gson: Gson? = null
    private var pref: SharedPreferences? = null
    private var lJson: String? = null
    private var tJson: String? = null

    constructor(gContext: Context) {
        context = gContext
        setUp()
    }

    constructor(gContext: Context, gLocation: Location) {
        context = gContext
        setUp()
        storeLocation(gLocation)
        storeTimes(Utils.getTimes(context, gLocation))
        storeStrTimes(getStrTimes(gLocation))
    }

    /**
     * It sets up the shared preferences and the gson object.
     */
    private fun setUp() {
        pref = PreferenceManager.getDefaultSharedPreferences(context)
        gson = Gson()
        Log.d(Global.TAG, "HERE")
    }

    /**
     * Store the location in the shared preferences
     *
     * @param gLocation The Location object that you want to store.
     */
    private fun storeLocation(gLocation: Location?) {
        val loc = MyLocation(gLocation!!)
        val editor: SharedPreferences.Editor = pref!!.edit()
        lJson = gson!!.toJson(loc)
        editor.putString("stored location", lJson)
        editor.apply()
    }

    /**
     * Stores the times in the array times in the shared preferences
     *
     * @param times The array of Calendar objects that you want to store.
     */
    private fun storeTimes(times: Array<Calendar?>) {
        val editor: SharedPreferences.Editor = pref!!.edit()
        tJson = gson!!.toJson(times)
        editor.putString("stored times", tJson)
        editor.apply()
    }

    /**
     * It stores the times in the shared preferences.
     *
     * @param times The array of times to store.
     */
    private fun storeStrTimes(times: Array<String?>?) {
        val editor: SharedPreferences.Editor = pref!!.edit()
        tJson = gson!!.toJson(times)
        editor.putString("stored string times", tJson)
        editor.apply()
    }

    /**
     * Retrieve the stored location from the shared preferences
     *
     * @return The location object.
     */
    fun retrieveLocation(): Location? {
        Log.d(Global.TAG, "HERE1")
        lJson = pref!!.getString("stored location", "")
        val myLocation: MyLocation? = gson!!.fromJson(lJson, MyLocation::class.java)
        return if (myLocation == null) null else MyLocation.toLocation(myLocation)
    }

    /**
     * Retrieves the stored times from the shared preferences
     *
     * @return An array of Calendar objects.
     */
    fun retrieveTimes(): Array<Calendar?> {
        tJson = pref!!.getString("stored times", "")
        return gson!!.fromJson(tJson, Array<Calendar?>::class.java)
    }

    /**
     * Retrieves the stored string times from the shared preferences
     *
     * @return An array of strings.
     */
    fun retrieveStrTimes(): Array<String>? {
        tJson = pref!!.getString("stored string times", "")
        return gson!!.fromJson(tJson, Array<String>::class.java)
    }

    /**
     * This function takes a Location object and returns an array of strings representing the prayer
     * times for that location
     *
     * @param loc Location object
     * @return An array of strings.
     */
    private fun getStrTimes(loc: Location): Array<String?> {
        val calendar = Calendar.getInstance()
        val date = Date()
        calendar.time = date
        val timeZoneObj = TimeZone.getDefault()
        val millis = timeZoneObj.getOffset(date.time).toLong()
        val timezone = millis / 3600000.0
        return reformatTimes(
            PrayTimes(context).getPrayerTimes(
                calendar, loc.latitude,
                loc.longitude, timezone
            )
        )
    }

    /**
     * Given a list of strings, reformat the strings into a list of strings, where each string is a
     * time
     *
     * @param givenTimes an ArrayList of the times in the given prayer times
     * @return An array of strings.
     */
    private fun reformatTimes(givenTimes: ArrayList<String>): Array<String?> {
        val arr = arrayOfNulls<String>(givenTimes.size - 1)
        val prayerNames = context.resources.getStringArray(R.array.prayer_names)
        var index = 0
        for (i in 0..4) {
            arr[i] = """
                ${prayerNames[index]}
                ${givenTimes[index]}
                """.trimIndent()
            if (++index == 1) index = 2
        }
        return arr
    }
}