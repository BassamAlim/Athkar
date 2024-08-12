package bassamalim.hidaya.core.data.preferences.objects

import bassamalim.hidaya.core.enums.AyaRepeat
import bassamalim.hidaya.core.models.QuranPageBookmark
import bassamalim.hidaya.features.quranReader.ui.QuranViewType
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class QuranPreferences(
    val suraFavorites: PersistentMap<Int, Int> = persistentMapOf(),
    val viewType: QuranViewType = QuranViewType.PAGE,
    val textSize: Float = 30f,
    val ayaReciterId: Int = 13,
    val ayaRepeat: AyaRepeat = AyaRepeat.NONE,
    val shouldStopOnSuraEnd: Boolean = false,
    val shouldStopOnPageEnd: Boolean = false,
    val pageBookmark: QuranPageBookmark? = null,
    val searchMaxMatches: Int = 10,
    val shouldShowMenuTutorial: Boolean = true,
    val shouldShowReaderTutorial: Boolean = true,
    val werdPage: Int = 25,
    val isWerdDone: Boolean = false,
)