package bassamalim.hidaya.repository

import android.content.SharedPreferences
import android.content.res.Resources
import bassamalim.hidaya.Prefs
import bassamalim.hidaya.R
import bassamalim.hidaya.database.AppDatabase
import bassamalim.hidaya.utils.PrefUtils
import com.google.gson.Gson
import javax.inject.Inject

class TelawatRepo @Inject constructor(
    private val resources: Resources,
    private val pref: SharedPreferences,
    private val db: AppDatabase,
    private val gson: Gson
) {

    val language = PrefUtils.getLanguage(pref)

    fun getFavs() = db.telawatRecitersDao().getFavs()
    fun setFav(reciterId: Int, fav: Int) = db.telawatRecitersDao().setFav(reciterId, fav)

    fun getReciters() = db.telawatRecitersDao().getAll()

    fun getReciterTelawat(reciterId: Int) = db.telawatDao().getReciterTelawat(reciterId)

    fun getAllVersions() = db.telawatDao().all

    fun getRewaya(reciterId: Int, versionId: Int): String =
        db.telawatVersionsDao().getVersion(reciterId, versionId).getRewaya()

    fun getReciterName(reciterId: Int) = db.telawatRecitersDao().getName(reciterId)

    fun getSuraNames() = db.suarDao().getNames()
    fun getSuraNamesEn() = db.suarDao().getNamesEn()

    fun getSelectedVersions(): MutableList<Boolean> {
        val selectedVersions = mutableListOf<Boolean>()

        val json = PrefUtils.getString(pref, Prefs.SelectedRewayat)
        if (json.isNotEmpty()) {
            val boolArr = gson.fromJson(json, BooleanArray::class.java)
            boolArr.forEach { bool -> selectedVersions.add(bool) }
        }
        else getRewayat().forEach { _ -> selectedVersions.add(true) }

        return selectedVersions
    }

    fun getLastPlayedMediaId() = PrefUtils.getString(pref, Prefs.LastPlayedMediaId)

    fun updateFavorites() {
        val recitersJson = gson.toJson(db.telawatRecitersDao().getFavs())
        pref.edit()
            .putString(Prefs.FavoriteReciters.key, recitersJson)
            .apply()
    }

    fun getRewayat(): Array<String> = resources.getStringArray(R.array.rewayat)

    fun getLastPlayStr() = resources.getString(R.string.last_play)
    fun getSuraStr() = resources.getString(R.string.sura)
    fun getForReciterStr() = resources.getString(R.string.for_reciter)
    fun getInRewayaOfStr() = resources.getString(R.string.in_rewaya_of)
    fun getNoLastPlayStr() = resources.getString(R.string.no_last_play)

}