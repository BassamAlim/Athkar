package bassamalim.hidaya.features.supplicationsCategories

import androidx.lifecycle.ViewModel
import bassamalim.hidaya.core.enums.ListType
import bassamalim.hidaya.core.nav.Navigator
import bassamalim.hidaya.core.nav.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupplicationsCategoriesViewModel @Inject constructor(
    private val navigator: Navigator
): ViewModel() {

    fun onAllAthkarClick() {
        navigator.navigate(
            Screen.AthkarList(ListType.ALL.name)
        )
    }

    fun onFavoriteAthkarClick() {
        navigator.navigate(
            Screen.AthkarList(ListType.FAVORITES.name)
        )
    }

    fun onCategoryClick(category: Int) {
        navigator.navigate(
            Screen.AthkarList(
                type = ListType.CUSTOM.name,
                category = category.toString()
            )
        )
    }

}