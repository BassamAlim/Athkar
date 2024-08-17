package bassamalim.hidaya.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bassamalim.hidaya.R
import bassamalim.hidaya.core.ui.theme.AppTheme

@Composable
fun FilterDialog(
    shown: Boolean,
    title: String,
    itemTitles: List<String>,
    itemSelections: Map<Int, Boolean>,
    onDismiss: (Map<Int, Boolean>) -> Unit
) {
    val selections = itemSelections.toMutableMap()

    MyDialog(shown) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyText(
                title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            MyLazyColumn(
                Modifier.heightIn(0.dp, 300.dp),
                lazyList = {
                    itemsIndexed(itemTitles) { index, _ ->
                        CheckboxListItem(
                            title = itemTitles[index],
                            isChecked = selections[index]!!
                        ) { isSelected ->
                            selections[index] = isSelected
                        }
                    }
                }
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                MyText(
                    stringResource(R.string.select_all),
                    textColor = AppTheme.colors.accent,
                    modifier = Modifier.clickable {
                        selections.mapValues { true }
                    }
                )

                MyText(
                    stringResource(R.string.unselect_all),
                    modifier = Modifier.clickable {
                        selections.mapValues { false }
                    },
                    textColor = AppTheme.colors.accent
                )
            }

            MySquareButton(
                text = stringResource(R.string.select),
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                onDismiss(selections)
            }
        }
    }
}

@Composable
private fun CheckboxListItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = AppTheme.colors.accent,
                uncheckedColor = AppTheme.colors.text
            )
        )

        MyText(title)
    }
}