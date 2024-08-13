package bassamalim.hidaya.features.recitationsRecitersMenu

import android.content.res.Resources
import bassamalim.hidaya.R
import bassamalim.hidaya.core.data.database.AppDatabase
import bassamalim.hidaya.core.data.preferences.Preference
import bassamalim.hidaya.core.data.preferences.PreferencesDataSource
import com.google.gson.Gson
import javax.inject.Inject

class RecitationsRecitersMenuRepository @Inject constructor(
    private val res: Resources,
    private val preferencesDS: PreferencesDataSource,
    private val db: AppDatabase,
    private val gson: Gson
) {

    fun getLanguage() = preferencesDS.getLanguage()

    fun getFavs() = db.recitationRecitersDao().getIsFavorites()

    fun setFav(reciterId: Int, fav: Int) {
        db.recitationRecitersDao().setIsFavorite(reciterId, fav)
    }

    fun getReciters() = db.recitationRecitersDao().getAll()

    fun getReciterTelawat(reciterId: Int) =
        db.recitationsDao().getReciterTelawat(reciterId)

    fun getAllVersions() = db.recitationsDao().all

    fun getRewaya(reciterId: Int, versionId: Int): String =
        db.recitationVersionsDao().getVersion(reciterId, versionId).nameAr

    fun getReciterName(reciterId: Int) =
        db.recitationRecitersDao().getNameAr(reciterId)

    fun getSuraNames() = db.surasDao().getDecoratedNamesAr()

    fun getSuraNamesEn() = db.surasDao().getDecoratedNamesEn()

    fun getSelectedVersions(): MutableList<Boolean> {
        val selectedVersions = mutableListOf<Boolean>()

        val json = preferencesDS.getString(Preference.SelectedRewayat)
        if (json.isNotEmpty()) {
            val boolArr = gson.fromJson(json, BooleanArray::class.java)
            boolArr.forEach { bool -> selectedVersions.add(bool) }
        }
        else getRewayat().forEach { _ -> selectedVersions.add(true) }

        return selectedVersions
    }

    fun getLastPlayedMediaId() =
        preferencesDS.getString(Preference.LastPlayedMediaId)

    fun updateFavorites() {
        val recitersJson = gson.toJson(db.recitationRecitersDao().getIsFavorites())
        preferencesDS.setString(Preference.FavoriteReciters, recitersJson)
    }

    fun getRewayat(): Array<String> =
        res.getStringArray(R.array.rewayat)

    fun getLastPlayStr() = res.getString(R.string.last_play)
    fun getSuraStr() = res.getString(R.string.sura)
    fun getForReciterStr() = res.getString(R.string.for_reciter)
    fun getInRewayaOfStr() = res.getString(R.string.in_rewaya_of)
    fun getNoLastPlayStr() = res.getString(R.string.no_last_play)

}