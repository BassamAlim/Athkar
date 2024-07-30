package bassamalim.hidaya.features.dateEditor.data

import bassamalim.hidaya.core.data.preferences.dataSources.AppSettingsPreferencesDataSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DateEditorRepository @Inject constructor(
    private val appSettingsPrefsRepo: AppSettingsPreferencesDataSource
) {

    suspend fun getNumeralsLanguage() = appSettingsPrefsRepo.getNumeralsLanguage().first()

    fun getDateOffset() = appSettingsPrefsRepo.getDateOffset()

    suspend fun updateDateOffset(offset: Int) {
        appSettingsPrefsRepo.update { it.copy(
            dateOffset = offset
        )}
    }

}