package bassamalim.hidaya.dialogs

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.preference.PreferenceManager
import bassamalim.hidaya.R
import bassamalim.hidaya.activities.MainActivity
import bassamalim.hidaya.enums.NotificationType
import bassamalim.hidaya.enums.PID
import bassamalim.hidaya.helpers.Alarms
import bassamalim.hidaya.helpers.Keeper
import bassamalim.hidaya.ui.components.*
import bassamalim.hidaya.ui.theme.AppTheme

class PrayerDialog(
    private val context: Context, private val pid: PID, private val prayerName: String,
    private val shown: MutableState<Boolean>, private val refresh: () -> Unit
) {

    private val pref = PreferenceManager.getDefaultSharedPreferences(context)
    private val offsetMin = 30
    private val notificationType = mutableStateOf(NotificationType.None)
    private val offset = mutableStateOf(0)
    private var sliderProgress = 30F
    private val notificationTypes = mutableListOf(
        Pair(R.string.athan_speaker, R.drawable.ic_speaker),
        Pair(R.string.enable_notification, R.drawable.ic_sound),
        Pair(R.string.silent_notification, R.drawable.ic_silent),
        Pair(R.string.disable_notification, R.drawable.ic_block)
    ).toList()

    init {
        retrieveState()
    }

    private fun retrieveState() {
        val defaultState =
            if (pid == PID.SHOROUQ) NotificationType.None
            else NotificationType.Notification
        val notificationState = pref.getString("$pid notification_type", defaultState.name)!!
        notificationType.value = NotificationType.valueOf(notificationState)

        offset.value = pref.getInt("$pid offset", 0)
        sliderProgress = offset.value + offsetMin.toFloat()
    }

    @Composable
    fun Dialog() {
        MyDialog(
            shown,
            onDismiss = {
                refresh()

                Keeper(context, MainActivity.location!!)
                Alarms(context, pid)
            }
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyText(
                    String.format(context.getString(R.string.settings_of), prayerName),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 5.dp, bottom = 20.dp)
                )

                CustomRadioGroup(
                    options = notificationTypes,
                    selection = notificationType,
                    onSelect = { selection ->
                        pref.edit()
                            .putString("$pid notification_type", selection)
                            .apply()

                        Alarms(context, pid)
                    }
                )

                MyText(
                    stringResource(R.string.adjust_prayer_notification_time),
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, bottom = 5.dp),
                    textAlign = TextAlign.Start
                )

                MyValuedSlider(
                    initialValue = sliderProgress,
                    valueRange = 0F..60F,
                    modifier = Modifier.fillMaxWidth(),
                    progressMin = offsetMin,
                    sliderFraction = 0.875F,
                    onValueChange = { value ->
                        pref.edit()
                            .putInt("$pid offset", value.toInt())
                            .apply()
                    }
                )
            }
        }
    }

    @Composable
    fun CustomRadioGroup(
        options: List<Pair<Int, Int>>,
        selection: MutableState<NotificationType>,
        onSelect: (String) -> Unit
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            options.forEachIndexed { i, pair ->
                if (!(pid == PID.SHOROUQ && i == 0)) {
                    val text = stringResource(pair.first)

                    Box(
                        Modifier.padding(vertical = 6.dp)
                    ) {
                        MyClickableSurface(
                            padding = PaddingValues(vertical = 0.dp),
                            modifier =
                            if (i == selection.value.ordinal)
                                Modifier.border(
                                    width = 3.dp,
                                    color = AppTheme.colors.accent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            else Modifier,
                            onClick = {
                                selection.value = NotificationType.values()[i]
                                onSelect(selection.value.name)
                            }
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp, horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(pair.second),
                                    contentDescription = text
                                )

                                MyText(text,
                                    textColor =
                                    if (i == selection.value.ordinal) AppTheme.colors.accent
                                    else AppTheme.colors.text,
                                    modifier = Modifier.padding(start = 20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}