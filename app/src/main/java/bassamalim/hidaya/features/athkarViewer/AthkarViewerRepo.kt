package bassamalim.hidaya.features.athkarViewer

import android.content.SharedPreferences
import bassamalim.hidaya.core.data.Prefs
import bassamalim.hidaya.core.data.database.AppDatabase
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.utils.PrefUtils
import javax.inject.Inject

class AthkarViewerRepo @Inject constructor(
    private val sp: SharedPreferences,
    private val db: AppDatabase
) {

    fun getLanguage() = PrefUtils.getLanguage(sp)

    fun getTextSize() = PrefUtils.getFloat(sp, Prefs.AthkarTextSize)

    fun setTextSize(textSize: Float) {
        sp.edit()
            .putInt(Prefs.AthkarTextSize.key, textSize.toInt())
            .apply()
    }

    fun getTitle(id: Int): String {
        return when(getLanguage()) {
            Language.ARABIC -> db.athkarDao().getName(id)
            Language.ENGLISH -> db.athkarDao().getNameEn(id)
        }
    }

    fun getThikrParts(id: Int) = db.athkarPartsDao().getThikrParts(id)

}