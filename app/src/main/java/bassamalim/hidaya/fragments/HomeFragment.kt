package bassamalim.hidaya.fragments

import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import bassamalim.hidaya.R
import bassamalim.hidaya.activities.MainActivity
import bassamalim.hidaya.databinding.FragmentHomeBinding
import bassamalim.hidaya.helpers.PrayTimes
import bassamalim.hidaya.other.Utils
import java.util.*

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var pref: SharedPreferences
    private lateinit var location: Location
    private lateinit var times: Array<Calendar?>
    private lateinit var formattedTimes: ArrayList<String>
    private lateinit var tomorrowFajr: Calendar
    private lateinit var formattedTomorrowFajr: String
    private var timer: CountDownTimer? = null
    private var upcoming = 0
    private var tomorrow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        if (MainActivity.located) setupPrayersCard()

        setupTelawatRecordCard()
        setupQuranRecordCard()

        return root
    }

    private fun setupPrayersCard() {
        location = MainActivity.location!!
        getTimes(location)
        setupUpcomingPrayer()
    }

    private fun getTimes(location: Location) {
        val prayTimes = PrayTimes(context!!)
        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance()
        tomorrow[Calendar.DATE] = tomorrow[Calendar.DATE] + 1

        val timeZoneObj = TimeZone.getDefault()
        val millis = timeZoneObj.getOffset(today.time.time).toLong()
        val timezone = millis / 3600000.0

        times = prayTimes.getPrayerTimesArray(
            today, location.latitude, location.longitude, timezone
        )
        formattedTimes = prayTimes.getPrayerTimes(
            today, location.latitude, location.longitude, timezone
        )

        tomorrowFajr = prayTimes.getTomorrowFajr(
            tomorrow, location.latitude, location.longitude, timezone
        )
        formattedTomorrowFajr = prayTimes.getPrayerTimes(
            tomorrow, location.latitude, location.longitude, timezone
        )[0]
    }

    private fun setupUpcomingPrayer() {
        upcoming = findUpcoming()

        tomorrow = false
        if (upcoming == -1) {
            tomorrow = true
            upcoming = 0
        }

        binding!!.prayerNameTv.text = resources.getStringArray(R.array.prayer_names)[upcoming]
        if (tomorrow) binding!!.prayerTimeTv.text = formattedTomorrowFajr
        else binding!!.prayerTimeTv.text = formattedTimes[upcoming]

        var till = times[upcoming]!!.timeInMillis
        if (tomorrow) till = tomorrowFajr.timeInMillis

        count(till)
    }

    private fun count(till: Long) {
        val restart = booleanArrayOf(true)
        timer = object : CountDownTimer(till - System.currentTimeMillis(),
            1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hours = millisUntilFinished / (60 * 60 * 1000) % 24
                val minutes = millisUntilFinished / (60 * 1000) % 60
                val seconds = millisUntilFinished / 1000 % 60

                val hms = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

                if (context != null)
                    binding!!.remainingTimeTv.text = String.format(
                        getString(R.string.remaining), Utils.translateNumbers(requireContext(),
                            hms, true))
                else {
                    restart[0] = false
                    timer?.cancel()
                }
            }

            override fun onFinish() {
                if (restart[0]) setupUpcomingPrayer()
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

    private fun setupTelawatRecordCard() {
        val millis = pref.getLong("telawat_playback_record", 0L)

        val hours = millis / (60 * 60 * 1000) % 24
        val minutes = millis / (60 * 1000) % 60
        val seconds = millis / 1000 % 60
        var hms = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

        if (pref.getString(getString(R.string.language_key),
                getString(R.string.default_language)) == "ar")
            hms = Utils.translateNumbers(requireContext(), hms, false)

        binding!!.telawatTimeDuration.text = hms
    }

    private fun setupQuranRecordCard() {
        var str = pref.getInt("quran_pages_record", 0).toString()

        if (pref.getString(getString(R.string.language_key),
                getString(R.string.default_language)) == "ar")
            str = Utils.translateNumbers(requireContext(), str, false)

        binding!!.quranPagesNum.text = str
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        timer?.cancel()
    }

}