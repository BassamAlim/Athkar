package bassamalim.hidaya.repository

import android.content.SharedPreferences
import android.location.Location
import bassamalim.hidaya.Prefs
import bassamalim.hidaya.database.AppDatabase
import bassamalim.hidaya.database.dbs.CityDB
import bassamalim.hidaya.database.dbs.CountryDB
import bassamalim.hidaya.enum.Language
import bassamalim.hidaya.utils.LocUtils
import bassamalim.hidaya.utils.PrefUtils
import javax.inject.Inject

class LocationPickerRepo @Inject constructor(
    val pref: SharedPreferences,
    val db: AppDatabase
) {

    val language = Language.valueOf(PrefUtils.getString(pref, Prefs.Language))

    fun getCountries(): List<CountryDB> {
        return db.countryDao().getAll().sortedBy { countryDB: CountryDB ->
            if (language == Language.ENGLISH) countryDB.nameEn
            else countryDB.nameAr
        }
    }

    fun getCities(countryId: Int): List<CityDB> {
        return if (language == Language.ENGLISH) db.cityDao().getTopEn(countryId, "").toList()
        else db.cityDao().getTopAr(countryId, "").toList()
    }

    fun storeLocation(countryId: Int, cityId: Int) {
        val city = db.cityDao().getCity(cityId)

        pref.edit()
            .putInt(Prefs.CountryID.key, countryId)
            .putInt(Prefs.CityID.key, cityId)
            .apply()

        val location = Location("")
        location.latitude = city.latitude
        location.longitude = city.longitude

        LocUtils.storeLocation(pref, city.latitude, city.longitude)
    }

}