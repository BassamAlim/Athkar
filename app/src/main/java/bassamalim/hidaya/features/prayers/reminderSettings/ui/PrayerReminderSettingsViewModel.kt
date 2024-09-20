package bassamalim.hidaya.features.prayers.reminderSettings.ui

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.enums.PID
import bassamalim.hidaya.core.nav.Navigator
import bassamalim.hidaya.features.prayers.reminderSettings.domain.PrayerReminderSettingsDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerReminderSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    domain: PrayerReminderSettingsDomain,
    private val navigator: Navigator
): ViewModel() {

    private val pid = PID.valueOf(savedStateHandle.get<String>("pid") ?: "")

    val offsetMin = domain.offsetMin
    lateinit var numeralsLanguage: Language

    private val _uiState = MutableStateFlow(PrayerReminderSettingsUiState(
        pid = pid,
        prayerName = domain.getPrayerName(pid)
    ))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            numeralsLanguage = domain.getNumeralsLanguage()

            _uiState.update { it.copy(
                offset = domain.getOffset(pid)
            )}
        }
    }

    fun onOffsetChange(offset: Int) {
        _uiState.update { it.copy(
            offset = offset
        )}
    }

    fun onSave() {
        navigator.navigateBackWithResult(
            data = Bundle().apply {
                putInt("offset", _uiState.value.offset)
            }
        )
    }

    fun onDismiss() {
        navigator.navigateBackWithResult(data = null)
    }

}