package bassamalim.hidaya.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import bassamalim.hidaya.R
import bassamalim.hidaya.databinding.ActivityWelcomeBinding
import bassamalim.hidaya.fragments.LocationFragment
import bassamalim.hidaya.fragments.SettingsFragment
import bassamalim.hidaya.other.Utils

class WelcomeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.myOnActivityCreated(this)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().replace(
                R.id.frame, SettingsFragment.newInstance(true)
            ).commit()

        setListeners()
    }

    private fun setListeners() {
        binding.saveBtn.setOnClickListener {
            binding.saveBtn.visibility = View.GONE

            supportFragmentManager.beginTransaction().replace(
                R.id.frame, LocationFragment.newInstance("initial")
            ).commit()

            val editor: SharedPreferences.Editor =
                PreferenceManager.getDefaultSharedPreferences(this).edit()
            editor.putBoolean("new_user", false)
            editor.apply()
        }
    }

}