package bassamalim.hidaya.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.floor

@Composable
fun MySlider(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {}
) {
    Slider(
        value = value,
        valueRange = valueRange,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        colors = SliderDefaults.colors(
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.onPrimaryContainer,
            thumbColor = MaterialTheme.colorScheme.primary
        ),
        onValueChangeFinished = onValueChangeFinished
    )
}

@Composable
fun MyValuedSlider(
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
    progressMin: Float = 0f,
    sliderFraction: Float = 0.8F,
    enabled: Boolean = true,
    valueFormatter: (String) -> String = { it },
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit = {},
) {
    var sliderText = valueFormatter((value - progressMin).toInt().toString()).let {
        if (progressMin != 0f && it.toInt() > 0) "+$it" else it
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MySlider(
            value = value,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth(fraction = sliderFraction),
            enabled = enabled,
            onValueChange = { newValue ->
                val progress = newValue - progressMin
                sliderText = valueFormatter(floor(progress).toInt().toString())
                onValueChange(progress)
            },
            onValueChangeFinished = onValueChangeFinished
        )

        MyText(
            text = sliderText,
            textColor = MaterialTheme.colorScheme.primary
        )
    }
}