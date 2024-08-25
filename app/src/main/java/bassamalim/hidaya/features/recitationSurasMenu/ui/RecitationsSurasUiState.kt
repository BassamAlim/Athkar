package bassamalim.hidaya.features.recitationSurasMenu.ui

import bassamalim.hidaya.core.enums.DownloadState

data class RecitationsSurasUiState(
    val title: String = "",
    val downloadStates: Map<Int, DownloadState> = emptyMap(),
    val searchText: String = ""
)