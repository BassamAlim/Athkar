package bassamalim.hidaya.core.data.database.daos

import androidx.room.Dao
import androidx.room.Query
import bassamalim.hidaya.core.data.database.dbs.ThikrsDB

@Dao
interface ThikrsDao {
    @Query("SELECT * FROM thikrs WHERE athkar_id = :id Order By thikr_id")
    fun getThikrs(id: Int): List<ThikrsDB>
}