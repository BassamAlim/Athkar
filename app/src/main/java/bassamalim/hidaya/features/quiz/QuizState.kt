package bassamalim.hidaya.features.quiz

import bassamalim.hidaya.core.enums.Language

data class QuizState(
    val questionIdx: Int = 0,
    val question: String = "",
    val answers: List<String> = emptyList(),
    val selection: Int = -1,
    val allAnswered: Boolean = false,
    val prevBtnEnabled: Boolean = false,
    val nextBtnEnabled: Boolean = true,
    val numeralsLanguage: Language = Language.ARABIC
)