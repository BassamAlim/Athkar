package bassamalim.hidaya.features.radio

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import bassamalim.hidaya.R
import bassamalim.hidaya.core.ui.components.MyPlayerBtn
import bassamalim.hidaya.core.ui.components.MyScaffold
import bassamalim.hidaya.core.ui.components.MyText
import bassamalim.hidaya.features.radio.RadioClientVM

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RadioClientUI(
    vm: RadioClientVM = hiltViewModel()
) {
    val st by vm.uiState.collectAsState()
    val activity = LocalContext.current as Activity

    DisposableEffect(key1 = vm) {
        vm.onStart(activity)
        onDispose { vm.onStop() }
    }

    MyScaffold(stringResource(R.string.quran_radio)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyText(
                text = stringResource(R.string.holy_quran_radio),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 50.dp)
            )

            MyPlayerBtn(state = st.btnState, padding = 10.dp) {
                vm.onPlayPause()
            }
        }
    }
}