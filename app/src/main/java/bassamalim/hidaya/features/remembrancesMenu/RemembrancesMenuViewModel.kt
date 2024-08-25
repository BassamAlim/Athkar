package bassamalim.hidaya.features.remembrancesMenu

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import bassamalim.hidaya.core.data.database.models.Remembrance
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.enums.ListType
import bassamalim.hidaya.core.nav.Navigator
import bassamalim.hidaya.core.nav.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RemembrancesMenuViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: RemembrancesMenuRepository,
    private val navigator: Navigator
): ViewModel() {

    private val type = savedStateHandle.get<String>("type") ?: ListType.ALL.name
    private val category = savedStateHandle.get<Int>("category")?: 0

    private val _uiState = MutableStateFlow(RemembrancesMenuState(
        language = repo.getLanguage(),
        listType = ListType.valueOf(type),
        items = getItems()
    ))
    val uiState = _uiState.asStateFlow()

    private fun getItems(): List<AthkarItem> {
        val remembrances = repo.getRemembrances(type, category)
        val items = mutableListOf<AthkarItem>()

        val isEng = _uiState.value.language == Language.ENGLISH
        for (remembrance in remembrances) {
            if (isEng && !hasEn(remembrance)) continue

            val name =
                if (isEng) remembrance.nameEn!!
                else remembrance.nameAr!!

            items.add(
                AthkarItem(
                    id = remembrance.id,
                    category_id = remembrance.categoryId,
                    name = name,
                    favorite = mutableIntStateOf(remembrance.isFavorite)
                )
            )
        }

        return if (_uiState.value.searchText.isEmpty()) items
        else items.filter { it.name.contains(_uiState.value.searchText, true) }
    }

    private fun hasEn(remembrance: Remembrance): Boolean {
        val remembrancePassages = repo.getRemembrancePassages(remembrance.id)

        for (i in remembrancePassages.indices) {
            val t = remembrancePassages[i]
            if (t.textEn != null && t.textEn.length > 1) return true
        }
        return false
    }

    fun onFavoriteCLick(item: AthkarItem) {
        if (item.favorite.value == 0) {
            repo.setFavorite(item.id, 1)
            item.favorite.value = 1
        }
        else {
            repo.setFavorite(item.id, 0)
            item.favorite.value = 0
        }

        repo.updateFavorites()
    }

    fun onItemClick(item: AthkarItem) {
        navigator.navigate(
            Screen.RemembranceReader(
                id = item.id.toString()
            )
        )
    }

    fun onSearchChange(text: String) {
        _uiState.update { it.copy(
            items = getItems(),
            searchText = text
        )}
    }

    fun getName() = repo.getName(_uiState.value.language, category)

}