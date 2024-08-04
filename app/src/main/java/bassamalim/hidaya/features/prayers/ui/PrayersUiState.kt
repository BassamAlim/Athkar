package bassamalim.hidaya.features.prayers.ui

data class PrayersUiState(
    val isLocationAvailable: Boolean = false,
    val locationName: String = "",
    val prayersData: List<PrayerCardData> = emptyList(),
    val isNoDateOffset: Boolean = true,
    val dateText: String = "",
    val settingsDialogShown: Boolean = false,
    val tutorialDialogShown: Boolean = false,
    val shouldShowLocationFailedToast: Boolean = false,
)