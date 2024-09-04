package bassamalim.hidaya.features.remembrances.remembranceCategories.ui

import androidx.lifecycle.ViewModel
import bassamalim.hidaya.core.enums.ListType
import bassamalim.hidaya.core.nav.Navigator
import bassamalim.hidaya.core.nav.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RemembranceCategoriesViewModel @Inject constructor(
    private val navigator: Navigator
): ViewModel() {

    fun onAllRemembrancesClick() {
        navigator.navigate(
            Screen.RemembrancesMenu(ListType.ALL.name)
        )
    }

    fun onFavoriteRemembrancesClick() {
        navigator.navigate(
            Screen.RemembrancesMenu(ListType.FAVORITES.name)
        )
    }

    fun onCategoryClick(category: Int) {
        navigator.navigate(
            Screen.RemembrancesMenu(
                type = ListType.CUSTOM.name,
                category = category.toString()
            )
        )
    }

}