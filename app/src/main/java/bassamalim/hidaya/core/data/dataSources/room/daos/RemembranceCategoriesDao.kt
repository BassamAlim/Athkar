package bassamalim.hidaya.core.data.dataSources.room.daos

import androidx.room.Dao
import androidx.room.Query

@Dao
interface RemembranceCategoriesDao {

    @Query("SELECT name_ar FROM remembrance_categories WHERE id = :id")
    fun getNameAr(id: Int): String

    @Query("SELECT name_en FROM remembrance_categories WHERE id = :id")
    fun getNameEn(id: Int): String

}