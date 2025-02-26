package bassamalim.hidaya.features.dateEditor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bassamalim.hidaya.R
import bassamalim.hidaya.core.ui.components.DialogDismissButton
import bassamalim.hidaya.core.ui.components.DialogSubmitButton
import bassamalim.hidaya.core.ui.components.DialogTitle
import bassamalim.hidaya.core.ui.components.MyIconButton
import bassamalim.hidaya.core.ui.components.MyText

@Composable
fun DateEditorDialog(viewModel: DateEditorViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = viewModel::onDismiss,
        dismissButton = { DialogDismissButton { viewModel.onDismiss() } },
        confirmButton = {
            DialogSubmitButton(text = stringResource(R.string.save), onSubmit = viewModel::onSave)
        },
        title = { DialogTitle(stringResource(R.string.adjust_date)) },
        text = { DialogContent(viewModel, state) }
    )
}

@Composable
private fun DialogContent(viewModel: DateEditorViewModel, state: DateEditorUiState) {
    Column {
        // Date offset
        MyText(
            text =
                if (state.isUnchanged) stringResource(R.string.unchanged)
                else state.dateOffsetText,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        DateOffsetEditor(
            dateText = state.dateText,
            onPreviousDayClick = viewModel::onPreviousDayClick,
            onNextDayClick = viewModel::onNextDayClick
        )
    }
}

@Composable
private fun DateOffsetEditor(
    dateText: String,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        MyIconButton(
            imageVector = Icons.AutoMirrored.Default.ArrowBackIos,
            contentColor = MaterialTheme.colorScheme.onSurface,
            onClick = onPreviousDayClick
        )

        MyText(text = dateText, fontSize = 22.sp)

        MyIconButton(
            imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
            contentColor = MaterialTheme.colorScheme.onSurface,
            onClick = onNextDayClick
        )
    }
}