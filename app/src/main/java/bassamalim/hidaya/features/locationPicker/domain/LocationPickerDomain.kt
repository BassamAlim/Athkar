package bassamalim.hidaya.features.locationPicker.domain

import bassamalim.hidaya.core.data.repositories.AppSettingsRepository
import bassamalim.hidaya.core.data.repositories.LocationRepository
import bassamalim.hidaya.core.enums.Language
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LocationPickerDomain @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val locationRepository: LocationRepository
) {

    private var countryId = -1

    fun setCountryId(id: Int) { countryId = id }

    suspend fun getLanguage() = appSettingsRepository.getLanguage().first()

    fun getCountries(language: Language) =
        locationRepository.getCountries(language = language)

    fun getCities(language: Language) =
        locationRepository.getCities(
            countryId = countryId,
            language = language
        )

}