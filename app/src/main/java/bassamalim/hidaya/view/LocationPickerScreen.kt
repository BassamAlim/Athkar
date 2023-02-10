package bassamalim.hidaya.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import bassamalim.hidaya.enums.Language
import bassamalim.hidaya.ui.components.*
import bassamalim.hidaya.viewmodel.LocationPickerVM

@Composable
fun LocationPickerUI(
    nc: NavController = rememberNavController(),
    vm: LocationPickerVM = hiltViewModel()
) {
    val state by vm.uiState.collectAsState()

    MyScaffold(
        topBar = {
            MyTopBar(
                title = stringResource(state.titleResId),
                onBack = { vm.onBack(nc) }
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchComponent(
                value = vm.searchText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            )

            MyLazyColumn(
                lazyList = {
                    items(state.items) { item ->
                        MyButton(
                            text =
                                if (vm.language == Language.ENGLISH) item.nameEn
                                else item.nameAr,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { vm.onSelect(item.id, nc) }
                        )
                    }
                }
            )
        }
    }
}