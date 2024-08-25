package bassamalim.hidaya.core.data.database.daos

import androidx.room.Dao
import androidx.room.Query
import bassamalim.hidaya.core.data.database.models.VerseRecitation

@Dao
interface VerseRecitationsDao {

    @Query("SELECT * FROM verse_recitations")
    fun getAll(): List<VerseRecitation>

    @Query("SELECT * FROM verse_recitations WHERE reciter_id = :reciterId")
    fun getReciter(reciterId: Int): List<VerseRecitation>

    @Query("SELECT COUNT(*) FROM verse_recitations")
    fun getSize(): Int

}