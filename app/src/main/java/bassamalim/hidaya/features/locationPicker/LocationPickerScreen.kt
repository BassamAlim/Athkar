package bassamalim.hidaya.features.locationPicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bassamalim.hidaya.core.enums.Language
import bassamalim.hidaya.core.ui.components.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.Dialog::class)
@Composable
fun LocationPickerUI(
    vm: LocationPickerVM = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val st by vm.uiState.collectAsStateWithLifecycle()

    MyScaffold(
        title = "",
        topBar = {
            MyTopBar(
                title = stringResource(st.titleResId),
                onBack = { vm.onBack(navigator) }
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchComponent(
                value = vm.searchText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                hint = stringResource(st.searchHintResId),
                onValueChange = { vm.onSearchTextChange(it) }
            )

            MyLazyColumn(
                lazyList = {
                    items(st.items) { item ->
                        MySquareButton(
                            text =
                            if (vm.language == Language.ENGLISH) item.nameEn
                            else item.nameAr,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { vm.onSelect(item.id, navigator) }
                        )
                    }
                }
            )
        }
    }
}