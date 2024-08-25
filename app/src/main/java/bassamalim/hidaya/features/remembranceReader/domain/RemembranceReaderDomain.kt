package bassamalim.hidaya.features.remembranceReader.domain

import bassamalim.hidaya.core.data.repositories.AppSettingsRepository
import bassamalim.hidaya.core.data.repositories.RemembrancesRepository
import bassamalim.hidaya.core.enums.Language
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RemembranceReaderDomain @Inject constructor(
    private val remembrancesRepository: RemembrancesRepository,
    private val appSettingsRepository: AppSettingsRepository
) {

    suspend fun getLanguage() = appSettingsRepository.getLanguage().first()

    fun getTextSize() = remembrancesRepository.getTextSize()

    suspend fun setTextSize(textSize: Float) {
        remembrancesRepository.setTextSize(textSize)
    }

    fun getTitle(id: Int, language: Language) =
        remembrancesRepository.getRemembranceName(id, language)

    fun getRemembrancePassages(id: Int) = remembrancesRepository.getRemembrancePassages(id)

}