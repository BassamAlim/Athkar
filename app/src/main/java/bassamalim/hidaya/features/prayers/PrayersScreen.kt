package bassamalim.hidaya.features.prayers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bassamalim.hidaya.R
import bassamalim.hidaya.core.enums.PID
import bassamalim.hidaya.core.ui.components.MyClickableSurface
import bassamalim.hidaya.core.ui.components.MyClickableText
import bassamalim.hidaya.core.ui.components.MyIconBtn
import bassamalim.hidaya.core.ui.components.MyRow
import bassamalim.hidaya.core.ui.components.MySurface
import bassamalim.hidaya.core.ui.components.MyText
import bassamalim.hidaya.core.ui.components.TutorialDialog
import bassamalim.hidaya.core.ui.theme.AppTheme
import bassamalim.hidaya.core.ui.theme.nsp

@Composable
fun PrayersUI(
    vm: PrayersVM,
    nc: NavController
) {
    val st by vm.uiState.collectAsStateWithLifecycle()

    DisposableEffect(key1 = vm) {
        vm.onStart()
        onDispose {}
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocationCard(vm, st, nc)

        PrayersSpace(vm, st)

        DayCard(vm, st)
    }

//    PrayerDialog(
//        shown = st.settingsDialogShown,
//        pid = st.settingsDialogPID,
//        notificationType = st.notificationTypes[st.settingsDialogPID.ordinal],
//        timeOffset = st.timeOffsets[st.settingsDialogPID.ordinal],
//        onNotificationTypeChange = { vm.onNotificationTypeChange(it) },
//        onOffsetChange = { vm.onTimeOffsetChange(it) },
//        onDismiss = { vm.onSettingsDialogDismiss() }
//    )

    TutorialDialog(
        shown = st.tutorialDialogShown,
        textResId = R.string.prayers_tips,
        onDismiss = { vm.onTutorialDialogDismiss(it) }
    )
}

@Composable
private fun LocationCard(
    vm: PrayersVM,
    st: PrayersState,
    nc: NavController
) {
    MySurface(
        Modifier.padding(top = 5.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MyText(
                text = st.locationName,
                modifier = Modifier
                    .widthIn(1.dp, 300.dp)
                    .padding(start = 15.dp)
            )

            MyIconBtn(
                iconId = R.drawable.ic_location,
                description = stringResource(R.string.locate),
                tint = AppTheme.colors.text,
                modifier = Modifier.padding(end = 8.dp),
                size = 32.dp
            ) {
                vm.onLocatorClick(nc)
            }
        }
    }
}

@Composable
private fun ColumnScope.PrayersSpace(
    vm: PrayersVM,
    st: PrayersState
) {
    Column(
        Modifier
            .weight(1F)
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        st.prayersData.forEachIndexed { i, data ->
            PrayerSpace(vm, st, i, data)

        }
    }
}

@Composable
private fun PrayerSpace(
    vm: PrayersVM,
    st: PrayersState,
    idx: Int,
    data: PrayerData
) {
    MyRow {
        PrayerCard(
            vm = vm,
            st = st,
            number = idx,
            data = data
        )

        ReminderCard(
            vm = vm,
            st = st,
            idx = idx
        )
    }
}

@Composable
private fun PrayerCard(
    vm: PrayersVM,
    st: PrayersState,
    number: Int,
    data: PrayerData
) {
    MyClickableSurface(
        cornerRadius = 15.dp,
        onClick = { vm.onPrayerClick(PID.values()[number]) }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Prayer name
            MyText(
                data.getText(),
                fontSize = 30.nsp,
                fontWeight = FontWeight.Medium
            )

            if (vm.location != null) {
                Row {
                    // Delay
                    MyText(vm.formatTimeOffset(data.timeOffset))

                    // Notification type
                    Icon(
                        painter = painterResource(
                            vm.getNotificationTypeIconID(data.notificationType)
                        ),
                        contentDescription = stringResource(R.string.notification_image_description),
                        tint = AppTheme.colors.accent,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    vm: PrayersVM,
    st: PrayersState,
    idx: Int
) {

}

@Composable
private fun DayCard(
    vm: PrayersVM,
    st: PrayersState
) {
    MySurface(
        Modifier.padding(bottom = 8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MyIconBtn(
                iconId = R.drawable.ic_left_arrow,
                description = stringResource(R.string.previous_day_button_description),
                tint = AppTheme.colors.text
            ) {
                vm.onPreviousDayClk()
            }

            MyClickableText(
                text = st.dateText,
                fontSize = 24.sp,
                textColor = AppTheme.colors.text,
                innerPadding = PaddingValues(vertical = 3.dp, horizontal = 15.dp)
            ) {
                vm.goToToday()
            }

            MyIconBtn(
                iconId = R.drawable.ic_right_arrow,
                description = stringResource(R.string.next_day_button_description),
                tint = AppTheme.colors.text
            ) {
                vm.onNextDayClk()
            }
        }
    }
}