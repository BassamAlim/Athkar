package bassamalim.hidaya.core.data.repositories

import bassamalim.hidaya.core.data.database.daos.RecitationNarrationsDao
import bassamalim.hidaya.core.data.database.daos.RecitationRecitersDao
import bassamalim.hidaya.core.data.database.daos.RecitationsDao
import bassamalim.hidaya.core.data.database.daos.VerseRecitationsDao
import bassamalim.hidaya.core.data.database.daos.VerseRecitersDao
import bassamalim.hidaya.core.data.preferences.dataSources.RecitationsPreferencesDataSource
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.enums.VerseRepeatMode
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecitationsRepository @Inject constructor(
    private val recitationsPreferencesDataSource: RecitationsPreferencesDataSource,
    private val recitationsDao: RecitationsDao,
    private val recitationRecitersDao: RecitationRecitersDao,
    private val verseRecitationsDao: VerseRecitationsDao,
    private val verseRecitersDao: VerseRecitersDao,
    private val recitationNarrationsDao: RecitationNarrationsDao
) {

    fun observeAllReciters() = recitationRecitersDao.observeAll()

    fun getReciterFavorites() = recitationsPreferencesDataSource.flow.map {
        it.reciterFavorites.toMap()
    }

    suspend fun setReciterFavorites(favorites: Map<Int, Int>) {
        recitationsPreferencesDataSource.update { it.copy(
            reciterFavorites = favorites.toPersistentMap()
        )}
    }

    suspend fun setReciterIsFavorite(reciterId: Int, isFavorite: Int) {
        recitationsPreferencesDataSource.update { it.copy(
            reciterFavorites = it.reciterFavorites.put(reciterId, isFavorite)
        )}
    }

    fun getReciterRecitations(reciterId: Int) = recitationsDao.getReciterRecitations(reciterId)

    fun getNarrationSelections() = recitationsPreferencesDataSource.flow.map {
        val selections = it.narrationSelections.toMap()
        selections.ifEmpty {
            val narrations = recitationNarrationsDao.getAll()
            narrations.associate { it.id to true }
        }
    }

    suspend fun setNarrationSelections(selections: Map<Int, Boolean>) {
        recitationsPreferencesDataSource.update { it.copy(
            narrationSelections = selections.toPersistentMap()
        )}
    }

    fun getRepeatMode() = recitationsPreferencesDataSource.flow.map {
        it.repeatMode
    }

    suspend fun setRepeatMode(mode: Int) {
        recitationsPreferencesDataSource.update { it.copy(
            repeatMode = mode
        )}
    }

    fun getShuffleMode() = recitationsPreferencesDataSource.flow.map {
        it.shuffleMode
    }

    suspend fun setShuffleMode(mode: Int) {
        recitationsPreferencesDataSource.update { it.copy(
            shuffleMode = mode
        )}
    }

    fun getAllNarrations() = recitationsDao.getAll()

    fun getLastPlayedMediaId() = recitationsPreferencesDataSource.flow.map {
        it.lastPlayedMediaId
    }

    suspend fun setLastPlayedMediaId(mediaId: String) {
        recitationsPreferencesDataSource.update { it.copy(
            lastPlayedMediaId = mediaId
        )}
    }

    fun getLastProgress() = recitationsPreferencesDataSource.flow.map {
        it.lastProgress
    }

    suspend fun setLastProgress(progress: Long) {
        recitationsPreferencesDataSource.update { it.copy(
            lastProgress = progress
        )}
    }

    fun getVerseReciterId() = recitationsPreferencesDataSource.flow.map {
        it.verseReciterId
    }

    suspend fun setVerseReciterId(verseReciterId: Int) {
        recitationsPreferencesDataSource.update { it.copy(
            verseReciterId = verseReciterId
        )}
    }

    fun getVerseRepeatMode() = recitationsPreferencesDataSource.flow.map {
        it.verseRepeatMode
    }

    suspend fun setVerseRepeatMode(verseRepeatMode: VerseRepeatMode) {
        recitationsPreferencesDataSource.update { it.copy(
            verseRepeatMode = verseRepeatMode
        )}
    }

    fun getShouldStopOnSuraEnd() = recitationsPreferencesDataSource.flow.map {
        it.shouldStopOnSuraEnd
    }

    suspend fun setShouldStopOnSuraEnd(shouldStopOnSuraEnd: Boolean) {
        recitationsPreferencesDataSource.update { it.copy(
            shouldStopOnSuraEnd = shouldStopOnSuraEnd
        )}
    }

    fun getShouldStopOnPageEnd() = recitationsPreferencesDataSource.flow.map {
        it.shouldStopOnPageEnd
    }

    suspend fun setShouldStopOnPageEnd(shouldStopOnPageEnd: Boolean) {
        recitationsPreferencesDataSource.update { it.copy(
            shouldStopOnPageEnd = shouldStopOnPageEnd
        )}
    }

    fun getVerseReciterNames() = verseRecitersDao.getNames()

    fun getReciterName(id: Int, language: Language) =
        if (language == Language.ARABIC) recitationRecitersDao.getNameAr(id)
        else recitationRecitersDao.getNameEn(id)

    fun getNarration(reciterId: Int, narrationId: Int) =
        recitationNarrationsDao.getNarration(reciterId, narrationId)

    fun getNarrations() = recitationNarrationsDao.getAll()

}