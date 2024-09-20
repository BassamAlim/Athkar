package bassamalim.hidaya.features.prayers.reminderSettings.ui

import bassamalim.hidaya.core.enums.PID

data class PrayerReminderSettingsUiState(
    val pid: PID,
    val prayerName: String,
    val offset: Int = 0
)