package bassamalim.hidaya.core.data.repositories

import bassamalim.hidaya.core.data.database.daos.RemembranceCategoriesDao
import bassamalim.hidaya.core.data.database.daos.RemembrancePassagesDao
import bassamalim.hidaya.core.data.database.daos.RemembrancesDao
import bassamalim.hidaya.core.data.preferences.dataSources.RemembrancePreferencesDataSource
import bassamalim.hidaya.core.enums.Language
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RemembrancesRepository @Inject constructor(
    private val remembrancePreferencesDataSource: RemembrancePreferencesDataSource,
    private val remembranceCategoriesDao: RemembranceCategoriesDao,
    private val remembrancesDao: RemembrancesDao,
    private val remembrancePassagesDao: RemembrancePassagesDao
) {

    fun getAllRemembranceCategories() = remembranceCategoriesDao.getAll()

    fun getRemembranceCategoryName(id: Int, language: Language) =
        if (language == Language.ARABIC) remembranceCategoriesDao.getNameAr(id)
        else remembranceCategoriesDao.getNameEn(id)

    fun observeAllRemembrances() = remembrancesDao.observeAll()

    fun observeFavorites() = remembrancesDao.observeFavorites()

    suspend fun setFavorite(id: Int, value: Boolean) {
        remembrancesDao.setFavoriteStatus(id = id, value = if (value) 1 else 0)

        setFavoritesBackup(
            remembrancesDao.observeFavoriteStatuses().first().mapIndexed { index, isFavorite ->
                index to isFavorite
            }.toMap()
        )
    }

    fun observeFavoriteStatuses() = remembrancesDao.observeFavoriteStatuses()

    suspend fun setFavoriteStatuses(favorites: Map<Int, Boolean>) {
        favorites.forEach { (id, value) ->
            remembrancesDao.setFavoriteStatus(id = id, value = if (value) 1 else 0)
        }
    }

    fun getFavoriteStatusesBackup() = remembrancePreferencesDataSource.flow.map { preferences ->
        preferences.favorites.map {
            it.key to (it.value == 1)
        }.toMap()
    }

    private suspend fun setFavoritesBackup(favorites: Map<Int, Int>) {
        remembrancePreferencesDataSource.update { it.copy(
            favorites = favorites.toPersistentMap()
        )}
    }

    fun observeCategoryRemembrances(categoryId: Int) =
        remembrancesDao.observeCategoryRemembrances(categoryId)

    fun getRemembranceNames(language: Language) =
        if (language == Language.ARABIC) remembrancesDao.getNamesAr()
        else remembrancesDao.getNamesEn()

    fun getRemembranceName(id: Int, language: Language) =
        if (language == Language.ARABIC) remembrancesDao.getNameAr(id)
        else remembrancesDao.getNameEn(id)

    fun getRemembrancePassages(remembranceId: Int) =
        remembrancePassagesDao.getRemembrancePassages(remembranceId)

    fun getTextSize() = remembrancePreferencesDataSource.flow.map {
        it.textSize
    }

    suspend fun setTextSize(textSize: Float) {
        remembrancePreferencesDataSource.update { it.copy(
            textSize = textSize
        )}
    }

}