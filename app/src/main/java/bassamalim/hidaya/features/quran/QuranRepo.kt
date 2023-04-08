package bassamalim.hidaya.features.quran

import android.content.SharedPreferences
import android.content.res.Resources
import bassamalim.hidaya.R
import bassamalim.hidaya.core.data.Prefs
import bassamalim.hidaya.core.data.database.AppDatabase
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.utils.PrefUtils
import com.google.gson.Gson
import javax.inject.Inject

class QuranRepo @Inject constructor(
    private val res: Resources,
    private val sp: SharedPreferences,
    private val db: AppDatabase,
    private val gson: Gson
) {

    fun getNumeralsLanguage() = PrefUtils.getNumeralsLanguage(sp)

    fun getBookmarkedPage() = PrefUtils.getInt(sp, Prefs.BookmarkedPage)

    fun getBookmarkedSura() = PrefUtils.getInt(sp, Prefs.BookmarkedSura)

    fun getAllSuras() = db.suarDao().getAll()

    fun getSuraNames(): List<String> {
        return if (PrefUtils.getLanguage(sp) == Language.ENGLISH) db.suarDao().getNamesEn()
        else db.suarDao().getNames()
    }

    fun getFavs() = db.suarDao().getFavs()

    fun setFav(suraId: Int, fav: Int) {
        db.suarDao().setFav(suraId, fav)
    }

    fun updateFavorites(favs: List<Int>) {
        val json = gson.toJson(favs.toIntArray())
        sp.edit()
            .putString(Prefs.FavoriteSuras.key, json)
            .apply()
    }

    fun getShowTutorial() = PrefUtils.getBoolean(sp, Prefs.ShowQuranTutorial)

    fun setDoNotShowAgain() {
        sp.edit()
            .putBoolean(Prefs.ShowQuranTutorial.key, false)
            .apply()
    }

    fun getSuraStr() = res.getString(R.string.sura)
    fun getPageStr() = res.getString(R.string.page)
    fun getNoBookmarkedPageStr() = res.getString(R.string.no_bookmarked_page)
    fun getBookmarkedPageStr() = res.getString(R.string.bookmarked_page)

}