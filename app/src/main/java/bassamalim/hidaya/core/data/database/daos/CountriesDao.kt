package bassamalim.hidaya.core.data.database.daos

import androidx.room.Dao
import androidx.room.Query
import bassamalim.hidaya.core.data.database.models.Country

@Dao
interface CountriesDao {

    @Query("SELECT * FROM countries")
    fun getAll(): List<Country>

    @Query("SELECT name_ar FROM countries WHERE id = :id")
    fun getNameAr(id: Int): String

    @Query("SELECT name_en FROM countries WHERE id = :id")
    fun getNameEn(id: Int): String

}