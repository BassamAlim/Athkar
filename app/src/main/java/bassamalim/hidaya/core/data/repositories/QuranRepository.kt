package bassamalim.hidaya.core.data.repositories

import bassamalim.hidaya.core.data.dataSources.preferences.dataSources.QuranPreferencesDataSource
import bassamalim.hidaya.core.data.dataSources.room.daos.SurasDao
import bassamalim.hidaya.core.data.dataSources.room.daos.VersesDao
import bassamalim.hidaya.core.di.ApplicationScope
import bassamalim.hidaya.core.di.DefaultDispatcher
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.models.QuranPageBookmark
import bassamalim.hidaya.core.models.Sura
import bassamalim.hidaya.features.quran.reader.ui.QuranViewType
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuranRepository @Inject constructor(
    private val quranPreferencesDataSource: QuranPreferencesDataSource,
    private val surasDao: SurasDao,
    private val versesDao: VersesDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
) {

    fun observeAllSuras(language: Language) = surasDao.observeAll().map {
        it.map { sura ->
            Sura(
                id = sura.id,
                decoratedName = when (language) {
                    Language.ARABIC -> sura.decoratedNameAr
                    Language.ENGLISH -> sura.decoratedNameEn
                },
                plainName = when (language) {
                    Language.ARABIC -> sura.plainNameAr
                    Language.ENGLISH -> sura.plainNameEn?: sura.plainNameAr
                },
                revelation = sura.revelation,
                isFavorite = sura.isFavorite == 1
            )
        }
    }

    suspend fun getDecoratedSuraNames(language: Language) = withContext(dispatcher) {
        if (language == Language.ENGLISH) surasDao.getDecoratedNamesEn()
        else surasDao.getDecoratedNamesAr()
    }

    suspend fun getPlainSuraNames() = withContext(dispatcher) {
        surasDao.getPlainNamesAr()
    }

    fun getSuraFavorites() = surasDao.observeIsFavorites().map {
        if (it.isEmpty()) {
            val favorites = (0..113).associateWith { false }
            setSuraFavorites(favorites)
            favorites
        }
        else it.mapIndexed { index, value -> index + 1 to (value == 1) }.toMap()
    }

    suspend fun setSuraFavoriteStatus(suraId: Int, isFavorite: Boolean) {
        withContext(dispatcher) {
            surasDao.setFavoriteStatus(suraId, if (isFavorite) 1 else 0)
        }
        setSuraFavoritesBackup(
            surasDao.observeIsFavorites().first().mapIndexed { index, value ->
                index + 1 to (value == 1)
            }.toMap()
        )
    }

    suspend fun setSuraFavorites(map: Map<Int, Boolean>) {
        withContext(dispatcher) {
            map.forEach { (suraId, isFavorite) ->
                surasDao.setFavoriteStatus(suraId, if (isFavorite) 1 else 0)
            }
        }
    }

    suspend fun getSuraPageNum(suraId: Int) = withContext(dispatcher) {
        surasDao.getSuraStartPage(suraId)
    }

    suspend fun getVersePageNum(verseId: Int) = withContext(dispatcher) {
        versesDao.getVersePageNum(verseId)
    }

    suspend fun getAllVerses() =  withContext(dispatcher) {
        versesDao.getAll()
    }
    
    fun getSuraFavoritesBackup() = quranPreferencesDataSource.getSuraFavorites()

    private suspend fun setSuraFavoritesBackup(suraFavorites: Map<Int, Boolean>) {
        quranPreferencesDataSource.updateSuraFavorites(suraFavorites.toPersistentMap())
    }

    fun getViewType() = quranPreferencesDataSource.getViewType()

    suspend fun setViewType(viewType: QuranViewType) {
        quranPreferencesDataSource.updateViewType(viewType)
    }

    fun getTextSize() = quranPreferencesDataSource.getTextSize()

    suspend fun setTextSize(textSize: Float) {
        quranPreferencesDataSource.updateTextSize(textSize)
    }

    fun getPageBookmark() = quranPreferencesDataSource.getPageBookmark()

    suspend fun setPageBookmark(bookmark: QuranPageBookmark?) {
        quranPreferencesDataSource.updatePageBookmark(bookmark)
    }

    fun getSearchMaxMatches() = quranPreferencesDataSource.getSearchMaxMatches()

    suspend fun setSearchMaxMatches(searchMaxMatches: Int) {
        quranPreferencesDataSource.updateSearchMaxMatches(searchMaxMatches)
    }

    fun getShouldShowMenuTutorial() = quranPreferencesDataSource.getShouldShowMenuTutorial()

    suspend fun setShouldShowMenuTutorial(shouldShowMenuTutorial: Boolean) {
        quranPreferencesDataSource.updateShouldShowMenuTutorial(shouldShowMenuTutorial)
    }

    fun getShouldShowReaderTutorial() = quranPreferencesDataSource.getShouldShowReaderTutorial()

    suspend fun setShouldShowReaderTutorial(shouldShowReaderTutorial: Boolean) {
        quranPreferencesDataSource.updateShouldShowReaderTutorial(shouldShowReaderTutorial)
    }

    fun getWerdPageNum() = quranPreferencesDataSource.getWerdPageNum()

    suspend fun setWerdPageNum(werdPageNum: Int) {
        quranPreferencesDataSource.updateWerdPageNum(werdPageNum)
    }

    fun isWerdDone() = quranPreferencesDataSource.getWerdDone()

    suspend fun setWerdDone(isWerdDone: Boolean) {
        quranPreferencesDataSource.updateWerdDone(isWerdDone)
    }

}